package com.yeoboya.lunch.api.v1.shop.service;

import com.yeoboya.lunch.api.v1.Item.domain.Item;
import com.yeoboya.lunch.api.v1.Item.repository.ItemRepository;
import com.yeoboya.lunch.api.v1.Item.request.ItemCreate;
import com.yeoboya.lunch.api.v1.Item.response.ItemResponse;
import com.yeoboya.lunch.api.v1.common.response.SlicePagination;
import com.yeoboya.lunch.api.v1.shop.domain.Shop;
import com.yeoboya.lunch.api.v1.shop.repository.ShopRepository;
import com.yeoboya.lunch.api.v1.shop.request.ShopAndItemCreate;
import com.yeoboya.lunch.api.v1.shop.request.ShopCreate;
import com.yeoboya.lunch.api.v1.shop.request.ShopSearch;
import com.yeoboya.lunch.api.v1.shop.response.ShopResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShopService {

    private final ShopRepository shopRepository;
    private final ItemRepository itemRepository;

    public ShopService(ShopRepository shopRepository, ItemRepository itemRepository) {
        this.shopRepository = shopRepository;
        this.itemRepository = itemRepository;
    }

    public ShopResponse create(ShopCreate create) {
        Shop shop = Shop.builder().
                name(create.getShopName()).
                build();
        return ShopResponse.from(shopRepository.save(shop));
    }


    @Transactional
    public List<ItemResponse> createShopAndAddItem(ShopAndItemCreate create) {
        Shop shopBuild = Shop.builder().
                name(create.getShopName()).
                build();
        Shop shop = shopRepository.save(shopBuild);

        //fixme
        if(create.getItems() == null){
            return null;
        }

        List<ItemResponse> itemResponses = new ArrayList<>();
        for (ItemCreate itemCreate : create.getItems()) {
            Item createItem = Item.builder().
                    shop(shop).
                    name(itemCreate.getItemName()).
                    price(itemCreate.getPrice()).
                    build();
            Item item = itemRepository.save(createItem);
            itemResponses.add(ItemResponse.from(item));
        }
        return itemResponses;
    }

    public Map<String, Object> shop(ShopSearch search, Pageable pageable) {
        Slice<Shop> shops = shopRepository.pageShops(search, pageable);
        List<ShopResponse> content = shops.getContent().stream()
                .map(ShopResponse::from)
                .collect(Collectors.toList());

        SlicePagination slicePagination = SlicePagination.builder()
                .page(shops.getNumber() + 1)
                .size(shops.getSize())
                .numberOfElements(shops.getNumberOfElements())
                .isFirst(shops.isFirst())
                .isLast(shops.isLast())
                .hasNext(shops.hasNext())
                .hasPrevious(shops.hasPrevious())
                .build();

        return Map.of(
                "list",content,
                "pagination", slicePagination);
    }

    public Shop selectShop(){
        List<Shop> shops = shopRepository.findAll();
        return shops.get(1);
    }

}
