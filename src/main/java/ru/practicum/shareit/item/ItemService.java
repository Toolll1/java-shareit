package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;

    private PageRequest pageableCreator(Integer from, Integer size) {

        return PageRequest.of(from / size, size);
    }

    public List<ItemDto> findAllByOwnerId(Integer userId, Integer from, Integer size) {

        log.info("I received a request to search for all the items added by the user to the id " + userId);
        Collection<Item> items = itemRepository.findAllByOwnerId(userId, pageableCreator(from, size));
        List<Booking> bookingList = bookingRepository.findAllByItemIn(items).stream()
                .filter((booking) -> booking.getStatus().equals(BookingStatus.WAITING) || booking.getStatus().equals(BookingStatus.APPROVED))
                .collect(Collectors.toList());
        Map<Item, List<Booking>> itemsMap = new HashMap<>();
        List<ItemDto> itemDtoList = new ArrayList<>();

        for (Item item : items) {

            List<Booking> itemsBooking = new ArrayList<>();

            for (Booking booking : bookingList) {
                if (booking.getItem().getId().equals(item.getId())) {

                    itemsBooking.add(booking);
                }
            }

            itemsMap.put(item, itemsBooking);
        }

        for (Map.Entry<Item, List<Booking>> itemListEntry : itemsMap.entrySet()) {
            itemDtoList.add(addData(itemListEntry.getKey(), itemListEntry.getValue()));
        }

        itemDtoList.sort(Comparator.comparing(ItemDto::getId));

        return itemDtoList;
    }


    public ItemDto findItemById(int itemId, Integer userId) {

        log.info("Searching for a item with an id " + itemId);

        Optional<Item> items = itemRepository.findById(itemId);

        if (items.isEmpty()) {
            throw new ObjectNotFoundException("There is no item with this id");
        }

        Item item = items.get();

        if (!item.getOwner().getId().equals(userId)) {

            ItemDto itemDto = ItemMapper.objectToDto(item);
            List<CommentDto> commentList = commentRepository.findAllByItemInOrderByCreatedDesc(List.of(item)).stream()
                    .map(CommentMapper::objectToDto)
                    .collect(Collectors.toList());

            itemDto.setComments(Objects.requireNonNullElseGet(commentList, ArrayList::new));

            return itemDto;
        }

        List<Booking> bookingList = bookingRepository.findAllByItem(item).stream()
                .filter((booking) -> booking.getStatus().equals(BookingStatus.WAITING) || booking.getStatus().equals(BookingStatus.APPROVED))
                .collect(Collectors.toList());

        return addData(items.get(), bookingList);
    }

    public ItemDto addData(Item item, List<Booking> bookingList) {

        ItemDto itemDto = ItemMapper.objectToDto(item);
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = bookingList.stream()
                .filter(x -> x.getStart().isBefore(now))
                .max(Comparator.comparing(Booking::getStart))
                .orElse(null);
        Booking nextBooking = bookingList.stream()
                .filter(x -> x.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
        List<CommentDto> commentList = commentRepository.findAllByItemInOrderByCreatedDesc(List.of(item)).stream()
                .map(CommentMapper::objectToDto)
                .collect(Collectors.toList());

        itemDto.setComments(Objects.requireNonNullElseGet(commentList, ArrayList::new));

        if (lastBooking == null) {
            itemDto.setLastBooking(null);
        } else {
            itemDto.setLastBooking(BookingMapper.objectToDtoMini(lastBooking));
        }
        if (nextBooking == null) {
            itemDto.setNextBooking(null);
        } else {
            itemDto.setNextBooking(BookingMapper.objectToDtoMini(nextBooking));
        }

        return itemDto;
    }

    public ItemDto createItem(ItemDto dto, Integer userId) {

        Item item = itemMapper.dtoToObject(dto, userId);
        ItemDto itemDto = ItemMapper.objectToDto(itemRepository.save(item));

        log.info("I received a request to create a item " + itemDto);

        return itemDto;
    }

    public ItemDto updateItem(ItemDto dto, Integer userId) {

        Item oldItem = itemRepository.findById(dto.getId()).get();
        Item item = itemMapper.dtoToObject(dto, userId);

        if (!item.getOwner().getId().equals(oldItem.getOwner().getId())) {

            throw new ObjectNotFoundException("You can't change the owner");
        }
        if (item.getDescription() != null) {

            oldItem.setDescription(item.getDescription());
        }
        if (item.getRequest() != null) {

            oldItem.getRequest().setId(item.getRequest().getId());
        }
        if (item.getAvailable() != null) {

            oldItem.setAvailable(item.getAvailable());
        }
        if (item.getName() != null) {

            oldItem.setName(item.getName());
        }

        Item newItem = itemRepository.save(oldItem);
        ItemDto itemDto = ItemMapper.objectToDto(newItem);

        log.info("I received a request to update a item\n" + itemDto);

        return itemDto;
    }

    public void deleteItem(int itemId, Integer userId) {

        if (findItemById(itemId, userId).getOwner().getId().equals(userId)) {
            itemRepository.deleteById(itemId);
        } else {
            throw new ValidateException("Only its owner can delete an item");
        }

        log.info("I received a request to delete a item with an id " + itemId);
    }

    public List<ItemDto> searchItems(String text, Integer from, Integer size) {

        log.info("I received a request to search for things whose name or description contains text: " + text);

        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Item> itemSet = new HashSet<>(itemRepository.search(text, pageableCreator(from, size)));

        return itemSet.stream()
                .map(ItemMapper::objectToDto)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    public CommentDto createComment(CommentDto dto, Integer userId, int itemId) {

        Item item = itemMapper.dtoToObject(findItemById(itemId, userId), userId);
        List<Booking> bookingList = Objects.requireNonNullElseGet(bookingRepository.findAllByItemIn(List.of(item)).stream()
                .filter((booking) -> booking.getStatus().equals(BookingStatus.WAITING) || booking.getStatus().equals(BookingStatus.APPROVED))
                .collect(Collectors.toList()), ArrayList::new);
        Comment comment = commentMapper.dtoToObject(dto, userId, item);
        CommentDto commentDto;

        for (Booking booking : bookingList) {

            if (booking.getBooker().getId().equals(userId)
                    && !booking.getStart().isAfter(dto.getCreated())) {

                commentDto = CommentMapper.objectToDto(commentRepository.save(comment));

                return commentDto;
            }
        }

        throw new BadRequestException("You can't add a comment to an item you didn't book");
    }
}
