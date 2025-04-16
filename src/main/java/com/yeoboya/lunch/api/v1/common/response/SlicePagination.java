package com.yeoboya.lunch.api.v1.common.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class SlicePagination {
    private final int page;   //현재 페이지의 번호를 반환합니다. 페이지 번호는 0부터 시작합니다.
    private final int size; //페이지 당 데이터의 수를 반환합니다.
    private final int numberOfElements; //현재 페이지에 있는 데이터의 수를 반환합니다.
    private final boolean isFirst;  //현재 페이지가 첫 페이지인지 여부를 반환합니다.
    private final boolean isLast;   //현재 페이지가 마지막 페이지인지 여부를 반환합니다.
    private final boolean hasNext;  //다음 페이지가 있는지 여부를 반환합니다.
    private final boolean hasPrevious;  //이전 페이지가 있는지 여부를 반환합니다.
}
