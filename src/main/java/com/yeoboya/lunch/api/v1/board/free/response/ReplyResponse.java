package com.yeoboya.lunch.api.v1.board.free.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yeoboya.lunch.api.v1.board.base.domain.Reply;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ReplyResponse {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long parentId;

    private Long replyId;

    private String writer;

    private String content;

    private Date date;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ReplyResponse> childReplies = new ArrayList<>();

    public static ReplyResponse of(Member member, Reply reply, List<Reply> allReplies) {
        ReplyResponse replyResponse = new ReplyResponse();
        replyResponse.setReplyId(reply.getId());
        replyResponse.setWriter(member.getName());
        replyResponse.setContent(reply.getContent());
        replyResponse.setDate(reply.getCreateDate());

        for (Reply childReply : allReplies) {
            Reply parent = childReply.getParentReply();

            // 현재 댓글(childReply)의 부모 댓글(parent)이 존재하고 그 ID가 reply 의 ID와 같다면, 이 childReply 는 reply 의 자식 댓글임을 확인
            if (parent != null && parent.getId().equals(reply.getId())) {
                ReplyResponse childReplyResponse = of(member, childReply, allReplies);
                childReplyResponse.setParentId(reply.getId());
                replyResponse.getChildReplies().add(childReplyResponse);
            }
        }
        return replyResponse;
    }

}
