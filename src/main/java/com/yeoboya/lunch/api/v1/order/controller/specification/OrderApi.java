package com.yeoboya.lunch.api.v1.order.controller.specification;

import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.order.request.*;
import com.yeoboya.lunch.api.v1.order.response.OrderDetailResponse;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "Order", description = "점심 주문 관리 API")
@RequestMapping("/order")
public interface OrderApi {

    @Operation(summary = "점심 주문 모집 시작", description = "점심 주문 모집을 시작합니다.")
    @PostMapping("/recruit/start")
    ResponseEntity<Response.Body> startLunchOrderRecruitment(@RequestBody @Valid OrderRecruitmentCreate orderRecruitmentCreate);

    @Operation(summary = "점심 주문 모집 리스트", description = "점심 주문 모집 목록을 조회합니다.")
    @GetMapping("/recruits")
    ResponseEntity<Response.Body> getLunchOrderRecruitmentList(OrderSearch search, Pageable pageable);

    @Operation(summary = "주문번호로 점심 주문 조회", description = "특정 주문번호에 해당하는 점심 주문을 조회합니다.")
    @GetMapping("/recruit/{orderId}")
    ResponseEntity<Response.Body> findLunchOrderByOrderId(@PathVariable Long orderId);

    @Operation(summary = "점심 주문 모집 참여", description = "점심 주문 모집에 참여합니다.")
    @PostMapping("/recruit/join")
    ResponseEntity<Response.Body> joinGroupOrder(@RequestBody @Valid GroupOrderJoin groupOrderJoin);

    @Operation(summary = "점심 주문 수정", description = "점심 주문 모집 참여 내용을 수정합니다.")
    @PatchMapping("/recruit/join")
    ResponseEntity<Response.Body> updateGroupOrder(@RequestBody GroupOrderJoinEdit groupOrderJoinEdit);

    @Operation(summary = "내 점심 주문 내역 (단건 조회)", description = "특정 그룹 주문 ID로 내 점심 주문 내역을 조회합니다.")
    @GetMapping("/recruit/history/join/{groupOrderId}")
    ResponseEntity<Response.Body> getMyJoinHistoryByOrderId(@PathVariable Long groupOrderId);

    @Operation(summary = "내 주문 내역 리스트 조회 (로그인 ID)", description = "내 점심 주문 내역을 로그인 ID를 기준으로 조회합니다.")
    @GetMapping("/recruit/histories/join/{loginId}")
    ResponseEntity<Response.Body> getMyJoinHistoryByLoginId(@PathVariable String loginId, Pageable pageable);

    @Operation(summary = "내 주문 모집 내역 리스트 조회", description = "내가 모집한 주문 내역을 로그인 ID 기준으로 조회합니다.")
    @GetMapping("/recruit/histories/{loginId}")
    ResponseEntity<Response.Body> getMyRecruitmentOrderHistory(@PathVariable String loginId, Pageable pageable);

    @Operation(summary = "주문 모집 상태 변경", description = "주문 모집 상태를 변경합니다.")
    @PatchMapping("/recruit/{orderId}")
    ResponseEntity<Response.Body> changeRecruitmentOrderStatus(@PathVariable Long orderId, @RequestBody OrderEdit orderEdit);

    @Operation(summary = "점심 주문 취소", description = "점심 주문을 취소합니다.")
    @DeleteMapping("/recruit/join/{groupOrderId}")
    ResponseEntity<Response.Body> cancelLunchOrder(@PathVariable Long groupOrderId);

    @Operation(summary = "점심 구매 내역 조회", description = "점심 구매 내역을 조회합니다.")
    @GetMapping("/purchase-recruits")
    ResponseEntity<Response.Body> purchaseRecruits(GroupOrderSearch search, Pageable pageable);
}