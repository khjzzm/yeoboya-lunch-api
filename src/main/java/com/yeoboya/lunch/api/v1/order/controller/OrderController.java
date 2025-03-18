package com.yeoboya.lunch.api.v1.order.controller;


import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.ErrorCode;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.common.response.Response.Body;
import com.yeoboya.lunch.api.v1.order.controller.specification.OrderApi;
import com.yeoboya.lunch.api.v1.order.request.*;
import com.yeoboya.lunch.api.v1.order.response.OrderDetailResponse;
import com.yeoboya.lunch.api.v1.order.service.OrderService;
import com.yeoboya.lunch.config.security.JwtTokenProvider;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.Duration;

@RestController
@RequestMapping("/order")
public class OrderController implements OrderApi {

    private final Bucket bucket;
    private final Response response;
    private final OrderService orderService;


    public OrderController(Response response, OrderService orderService, JwtTokenProvider jwtTokenProvider) {
        this.response = response;
        this.orderService = orderService;

        //10분에 10개의 요청을 처리할 수 있는 Bucket 생성
        Bandwidth limit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(10)));
        this.bucket = Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * 점심 주문 모집 시작
     */
    @PostMapping("/recruit/start")
    public ResponseEntity<Body> startLunchOrderRecruitment(@RequestBody @Valid OrderRecruitmentCreate orderRecruitmentCreate){
        if (bucket.tryConsume(1)) {
            OrderDetailResponse orderDetailResponse = orderService.startLunchOrderRecruitment(orderRecruitmentCreate);
            return response.success(Code.SAVE_SUCCESS, orderDetailResponse);
        }
        return response.fail(ErrorCode.TOO_MANY_REQUESTS);

    }

    /**
     * 점심 주문 모집 리스트
     */
    @GetMapping("/recruits")
    public ResponseEntity<Body> getLunchOrderRecruitmentList(OrderSearch search, Pageable pageable){
        return response.success(Code.SEARCH_SUCCESS, orderService.getLunchOrderRecruitmentList(search, pageable));
    }

    /**
     * 주문번호로 점심 주문 조회
     */
    @GetMapping("/recruit/{orderId}")
    public ResponseEntity<Body> findLunchOrderByOrderId(@PathVariable Long orderId){
        return response.success(Code.SEARCH_SUCCESS, orderService.findLunchOrderByOrderId(orderId));
    }

    /**
     * 점심 주문 모집 참여
     */
    @PostMapping("/recruit/join")
    public ResponseEntity<Body> joinGroupOrder(@RequestBody @Valid GroupOrderJoin groupOrderJoin){
        orderService.joinGroupOrder(groupOrderJoin);
        return response.success(Code.SAVE_SUCCESS);
    }


    /**
     * Updates a group order based on the given GroupOrderJoinEdit object.
     *
     * @param groupOrderJoinEdit The GroupOrderJoinEdit object containing the updated group order details.
     *                           It must have the orderId, groupOrderId, and orderItems fields set.
     *                           The orderItems list must contain OrderItemCreateEdit objects representing the updated order items.
     * @return The ResponseEntity object that represents the HTTP response with the updated group order details.
     *         The response body will contain a success code.
     */
    @PatchMapping("/recruit/join")
    public ResponseEntity<Body> updateGroupOrder(@RequestBody GroupOrderJoinEdit groupOrderJoinEdit){
        orderService.editGroupOrder(groupOrderJoinEdit);
        return response.success(Code.UPDATE_SUCCESS);
    }

    /**
     * 내 점심 주문 내역 (단건)
     * -groupOrderId 로 조회
     */
    @GetMapping("/recruit/history/join/{groupOrderId}")
    public  ResponseEntity<Body> getMyJoinHistoryByOrderId(@PathVariable Long groupOrderId){
        return response.success(Code.SEARCH_SUCCESS, orderService.getMyJoinHistoryByOrderId(groupOrderId));
    }

    /**
     * 내 주문 내역 리스트 조회 (이메일)
     */
    @GetMapping("/recruit/histories/join/{loginId}")
    public ResponseEntity<Body> getMyJoinHistoryByLoginId(@PathVariable String loginId, Pageable pageable){
        return response.success(Code.SEARCH_SUCCESS, orderService.getMyJoinHistoriesByLoginId(loginId, pageable));
    }


    /**
     * 내 주문 모집 내역 리스트 조회 (이메일)
     */
    @GetMapping("/recruit/histories/{loginId}")
    public ResponseEntity<Body> getMyRecruitmentOrderHistory(@PathVariable String loginId, Pageable pageable){
        return response.success(Code.SEARCH_SUCCESS, orderService.getMyRecruitmentOrderHistoriesByLoginId(loginId, pageable));
    }

    /**
     * 주문모집 상태변경
     */
    @PatchMapping("/recruit/{orderId}")
    public ResponseEntity<Body> changeRecruitmentOrderStatus(@PathVariable(value = "orderId") Long orderId,
                                                   @RequestBody OrderEdit orderEdit){
        orderService.changeRecruitmentOrderStatus(orderId, orderEdit);
        return response.success(Code.UPDATE_SUCCESS);
    }

    /**
     * 점심주문취소
     */
    @DeleteMapping("/recruit/join/{groupOrderId}")
    public ResponseEntity<Body> cancelLunchOrder(@PathVariable Long groupOrderId){
        orderService.cancelLunchOrder(groupOrderId);
        return response.success(Code.UPDATE_SUCCESS);
    }

    //------------------------------------------

    /**
     * 점심 구매 내역
     */
    @GetMapping("/purchase-recruits")
    public ResponseEntity<Body> purchaseRecruits(GroupOrderSearch search, Pageable pageable){
        return response.success(Code.SEARCH_SUCCESS, orderService.purchaseRecruits(search, pageable));
    }


}
