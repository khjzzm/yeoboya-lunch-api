package com.yeoboya.lunch.api.v1.order.service;

import com.yeoboya.lunch.api.v1.Item.domain.Item;
import com.yeoboya.lunch.api.v1.Item.repository.ItemRepository;
import com.yeoboya.lunch.api.v1.common.exception.EntityNotFoundException;
import com.yeoboya.lunch.api.v1.common.response.SlicePagination;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import com.yeoboya.lunch.api.v1.member.response.MemberResponse;
import com.yeoboya.lunch.api.v1.order.constants.OrderStatus;
import com.yeoboya.lunch.api.v1.order.domain.GroupOrder;
import com.yeoboya.lunch.api.v1.order.domain.Order;
import com.yeoboya.lunch.api.v1.order.domain.OrderItem;
import com.yeoboya.lunch.api.v1.order.repository.GroupOrderRepository;
import com.yeoboya.lunch.api.v1.order.repository.OrderItemRepository;
import com.yeoboya.lunch.api.v1.order.repository.OrderRepository;
import com.yeoboya.lunch.api.v1.order.request.*;
import com.yeoboya.lunch.api.v1.order.response.GroupOrderRecruitmentResponse;
import com.yeoboya.lunch.api.v1.order.response.GroupOrderResponse;
import com.yeoboya.lunch.api.v1.order.response.OrderDetailResponse;
import com.yeoboya.lunch.api.v1.order.response.OrderRecruitmentResponse;
import com.yeoboya.lunch.api.v1.shop.domain.Shop;
import com.yeoboya.lunch.api.v1.shop.repository.ShopRepository;
import com.yeoboya.lunch.api.v1.shop.response.ShopResponse;
import com.yeoboya.lunch.config.security.JwtTokenProvider;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;
    private final MemberRepository memberRepository;
    private final GroupOrderRepository groupOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;

    public OrderService(OrderRepository orderRepository, ShopRepository shopRepository, MemberRepository memberRepository, GroupOrderRepository groupOrderRepository, OrderItemRepository orderItemRepository, ItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.shopRepository = shopRepository;
        this.memberRepository = memberRepository;
        this.groupOrderRepository = groupOrderRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemRepository = itemRepository;
    }


    public OrderDetailResponse startLunchOrderRecruitment(OrderRecruitmentCreate orderRecruitmentCreate) {
        Member member = memberRepository.findByLoginId(orderRecruitmentCreate.getLoginId()).
                orElseThrow(() -> new EntityNotFoundException("Member not found - " + orderRecruitmentCreate.getLoginId()));

        Shop shop = shopRepository.findByName(orderRecruitmentCreate.getShopName()).
                orElseThrow(() -> new EntityNotFoundException("Shop not fount - " + orderRecruitmentCreate.getShopName()));

        Order order = Order.recruit(member, shop, orderRecruitmentCreate);
        Order save = orderRepository.save(order);

        return null;
    }

    public Map<String, Object> getLunchOrderRecruitmentList(OrderSearch search, Pageable pageable) {
        Slice<Order> orders = orderRepository.orderRecruits(search, pageable);
        List<OrderRecruitmentResponse> orderRecruitmentResponses = orders.getContent().stream()
                .map(OrderRecruitmentResponse::from)
                .collect(Collectors.toList());

        SlicePagination slicePagination = SlicePagination.builder()
                .page(orders.getNumber() + 1)
                .size(orders.getSize())
                .numberOfElements(orders.getNumberOfElements())
                .isFirst(orders.isFirst())
                .isLast(orders.isLast())
                .hasNext(orders.hasNext())
                .hasPrevious(orders.hasPrevious())
                .build();

        return Map.of(
                "list", orderRecruitmentResponses,
                "pagination", slicePagination);

    }

    public Map<String, Object> findLunchOrderByOrderId(Long orderNo) {
        Order order = orderRepository.findById(orderNo).orElseThrow(() -> new EntityNotFoundException("Order not found - " + orderNo));

        //주문모집정보
        OrderDetailResponse orderDetailResponse = OrderDetailResponse.of(order);

        //주문참가자정보
        List<GroupOrderResponse> groupOrderResponse = order.getGroupOrders().stream()
                .map((r) -> GroupOrderResponse.of(r, r.getMember(), r.getOrderItems()))
                .collect(Collectors.toList());

        //식당정보
        ShopResponse shopResponse = ShopResponse.from(order.getShop());

        //주문장 정보
        MemberResponse memberResponse = MemberResponse.from(order.getMember());

        return Map.of("order", orderDetailResponse,
                "shop", shopResponse,
                "orderMember", memberResponse,
                "group", groupOrderResponse
        );
    }

    public void joinGroupOrder(GroupOrderJoin groupOrderJoin) {
        Order order = orderRepository.findById(groupOrderJoin.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found - " + groupOrderJoin.getOrderId()));
        Member member = memberRepository.findByLoginId(groupOrderJoin.getLoginId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found - " + groupOrderJoin.getLoginId()));

        List<OrderItem> orderItems = groupOrderJoin.getOrderItems().stream()
                .map(orderItemCreate -> {
                    Item item = itemRepository.getItemByShopNameAndName(order.getShop().getName(), orderItemCreate.getItemName())
                            .orElseThrow(() -> new EntityNotFoundException("Item not found - " + orderItemCreate.getItemName()));

                    return OrderItem.createOrderItem(item, order, null, item.getPrice(), orderItemCreate.getOrderQuantity());
                })
                .collect(Collectors.toList());

        GroupOrder groupOrder = GroupOrder.createGroupOrder(order, member, orderItems);
        groupOrderRepository.save(groupOrder);
    }

    @Transactional
    public void editGroupOrder(GroupOrderJoinEdit groupOrderJoinEdit) {
        Order order = orderRepository.findById(groupOrderJoinEdit.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found - " + groupOrderJoinEdit.getOrderId()));
//        Member member = memberRepository.findByEmail(groupOrderJoinEdit.getEmail())
//                .orElseThrow(() -> new EntityNotFoundException("Member not found - " + groupOrderJoinEdit.getEmail()));
        GroupOrder groupOrder = groupOrderRepository.findById(groupOrderJoinEdit.getGroupOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Group order not found - " + groupOrderJoinEdit.getGroupOrderId()));

        Map<String, OrderItem> existingItems = groupOrder.getOrderItems().stream()
                .collect(Collectors.toMap(orderItem -> orderItem.getItem().getName(), orderItem -> orderItem));

        List<OrderItem> updatedOrderItems = groupOrderJoinEdit.getOrderItems().stream()
                .map(orderItemCreateEdit -> {
                    String itemName = orderItemCreateEdit.getItemName();
                    int orderQuantity = orderItemCreateEdit.getOrderQuantity();
                    if (existingItems.containsKey(itemName)) {
                        OrderItem existingOrderItem = existingItems.remove(itemName);
                        existingOrderItem.updateQuantity(orderQuantity);
                        return existingOrderItem;
                    } else {
                        Item item = itemRepository.getItemByShopNameAndName(order.getShop().getName(), itemName)
                                .orElseThrow(() -> new EntityNotFoundException("Item not found - " + itemName));
                        return OrderItem.createOrderItem(item, order, groupOrder, item.getPrice(), orderQuantity);
                    }
                })
                .collect(Collectors.toList());

        // Remove items not in the request
        existingItems.values().forEach(orderItem -> {
            orderItem.setGroupOrder(null);
            orderItem.setOrder(null);
            orderItemRepository.delete(orderItem);
        });

        groupOrder.getOrderItems().clear();
        groupOrder.getOrderItems().addAll(updatedOrderItems);
        groupOrderRepository.save(groupOrder);
    }

    public GroupOrderResponse getMyJoinHistoryByOrderId(Long groupOrderId) {
        return groupOrderRepository.findById(groupOrderId)
                .map(GroupOrderResponse::of)
                .orElseThrow(() -> new RuntimeException("모임 주문을 찾을 수 없습니다."));
    }

    public Map<String, Object> getMyJoinHistoriesByLoginId(String logInId, Pageable pageable) {
        Slice<GroupOrder> groupOrders = groupOrderRepository.getJoinHistoriesByLoginId(logInId, pageable);
        List<GroupOrderResponse> groupOrderResponses = groupOrders.getContent().stream()
                .map(groupOrder -> GroupOrderResponse.of(groupOrder, groupOrder.getMember(), groupOrder.getOrderItems()))
                .collect(Collectors.toList());

        SlicePagination slicePagination = SlicePagination.builder()
                .page(groupOrders.getNumber() + 1)
                .size(groupOrders.getSize())
                .numberOfElements(groupOrders.getNumberOfElements())
                .isFirst(groupOrders.isFirst())
                .isLast(groupOrders.isLast())
                .hasNext(groupOrders.hasNext())
                .hasPrevious(groupOrders.hasPrevious())
                .build();

        return Map.of(
                "list", groupOrderResponses,
                "pagination", slicePagination);

    }

    public Map<String, Object> getMyJoinHistoryByToken(Pageable pageable) {
        String currentUserLoginId = JwtTokenProvider.getCurrentUserLoginId();

        Slice<GroupOrder> groupOrders = groupOrderRepository.getJoinHistoriesByLoginId(currentUserLoginId, pageable);
        List<GroupOrderResponse> groupOrderResponses = groupOrders.getContent().stream()
                .map(groupOrder -> GroupOrderResponse.of(groupOrder, groupOrder.getMember(), groupOrder.getOrderItems()))
                .collect(Collectors.toList());

        SlicePagination slicePagination = SlicePagination.builder()
                .page(groupOrders.getNumber() + 1)
                .size(groupOrders.getSize())
                .numberOfElements(groupOrders.getNumberOfElements())
                .isFirst(groupOrders.isFirst())
                .isLast(groupOrders.isLast())
                .hasNext(groupOrders.hasNext())
                .hasPrevious(groupOrders.hasPrevious())
                .build();

        return Map.of(
                "list", groupOrderResponses,
                "pagination", slicePagination);

    }

    public List<OrderDetailResponse> getMyRecruitmentOrderHistoriesByLoginId(String loginId, Pageable pageable) {
        Slice<Order> orderHistory = orderRepository.findByMemberLoginId(loginId, pageable);

        return orderHistory.stream()
                .map(OrderDetailResponse::of)
                .collect(Collectors.toList());
    }

    public List<OrderDetailResponse> getMyRecruitmentOrderHistoriesByToken(Pageable pageable) {
        String memberLoginId = JwtTokenProvider.getCurrentUserLoginId();
        Slice<Order> orderHistory = orderRepository.findByMemberLoginId(memberLoginId, pageable);

        return orderHistory.stream()
                .map(OrderDetailResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void changeRecruitmentOrderStatus(Long orderId, OrderEdit orderEdit) {
        String currentUserEmail = JwtTokenProvider.getCurrentUserLoginId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found - " + orderId));

//        if (!order.getMember().getEmail().equals(currentUserEmail)) {
//            throw new SecurityException("Only the person who created the order can modify it.");
//        }

        order.setStatus(OrderStatus.valueOf(orderEdit.getStatus()));
    }

    @Transactional
    public void cancelLunchOrder(Long groupOrderId) {
        groupOrderRepository.deleteById(groupOrderId);
    }


    //--------------------------------------------------------------------------------------------------------------------------------

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found - " + orderId));
        order.setStatus(OrderStatus.CANCEL);
    }


    public Map<String, Object> purchaseRecruits(GroupOrderSearch search, Pageable pageable) {
        Slice<GroupOrder> groupOrders = groupOrderRepository.purchaseRecruits(search, pageable);
        List<GroupOrderRecruitmentResponse> groupOrderRecruitmentResponses = groupOrders.getContent().stream()
                .map(GroupOrderRecruitmentResponse::from)
                .collect(Collectors.toList());

        SlicePagination slicePagination = SlicePagination.builder()
                .page(groupOrders.getNumber() + 1)
                .size(groupOrders.getSize())
                .numberOfElements(groupOrders.getNumberOfElements())
                .isFirst(groupOrders.isFirst())
                .isLast(groupOrders.isLast())
                .hasNext(groupOrders.hasNext())
                .hasPrevious(groupOrders.hasPrevious())
                .build();

        return Map.of(
                "list", groupOrderRecruitmentResponses,
                "pagination", slicePagination);
    }

}
