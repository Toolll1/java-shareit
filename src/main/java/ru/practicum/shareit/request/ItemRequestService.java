package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemDtoMini;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserService userService;
    private final ItemRepository itemRepository;

    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Integer userId) {

        itemRequestDto.setCreated(LocalDateTime.now());

        ItemRequest itemRequest = itemRequestRepository.save(itemRequestMapper.dtoToObject(itemRequestDto, userId));

        return ItemRequestMapper.objectToDto(itemRequest);
    }

    public ItemRequestDto updateRequest(ItemRequestDto itemRequestDto, Integer userId) {

        ItemRequestDto oldItemRequestDto = findRequestById(itemRequestDto.getId(), userId);

        if (itemRequestDto.getDescription() != null) {
            oldItemRequestDto.setDescription(itemRequestDto.getDescription());
        }

        ItemRequest itemRequest = itemRequestRepository.save(itemRequestMapper.dtoToObject(oldItemRequestDto, userId));

        return ItemRequestMapper.objectToDto(itemRequest);
    }

    public List<ItemRequestDto> findAllRequest(Integer userId) {

        userService.findUserById(userId);

        List<ItemRequestDto> itemRequestDtoList = itemRequestRepository.findAll().stream()
                .map(ItemRequestMapper::objectToDto)
                .filter(x -> x.getRequestor().getId().equals(userId))
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .collect(Collectors.toList());

        for (ItemRequestDto itemRequestDto : itemRequestDtoList) {

            itemRequestDto.setItems(
                    itemRepository.findAllByRequestId(itemRequestDto.getId()).stream()
                            .map(ItemMapper::objectToDtoMini)
                            .sorted(Comparator.comparing(ItemDtoMini::getId))
                            .collect(Collectors.toList())
            );
        }

        return itemRequestDtoList;
    }

    public List<ItemRequestDto> findAllRequest(Integer userId, Integer from, Integer size) {

        if (from < 0 || size <= 0) {
            throw new BadRequestException("the from parameter must be greater than or equal to 0; size is greater than 0");
        }

        List<ItemRequestDto> itemRequestDtoList = itemRequestRepository
                .findAll(PageRequest.of(from / size, size, Sort.by("created").descending()))
                .stream()
                .map(ItemRequestMapper::objectToDto)
                .filter(x -> !x.getRequestor().getId().equals(userId))
                .collect(Collectors.toList());

        for (ItemRequestDto itemRequestDto : itemRequestDtoList) {

            itemRequestDto.setItems(
                    itemRepository.findAllByRequestId(itemRequestDto.getId()).stream()
                            .map(ItemMapper::objectToDtoMini)
                            .sorted(Comparator.comparing(ItemDtoMini::getId))
                            .collect(Collectors.toList())
            );
        }

        return itemRequestDtoList;
    }

    public ItemRequestDto findRequestById(int itemRequestId, Integer userId) {

        userService.findUserById(userId);
        Optional<ItemRequest> requests = itemRequestRepository.findById(itemRequestId);

        if (requests.isEmpty()) {
            throw new ObjectNotFoundException("There is no request with this id");
        }

        ItemRequestDto itemRequestDto = ItemRequestMapper.objectToDto(requests.get());

        itemRequestDto.setItems(
                itemRepository.findAllByRequestId(itemRequestId).stream()
                        .map(ItemMapper::objectToDtoMini)
                        .collect(Collectors.toList())
        );

        return itemRequestDto;
    }


    public void deleteRequest(int itemRequestId) {

        itemRequestRepository.deleteById(itemRequestId);
    }
}
