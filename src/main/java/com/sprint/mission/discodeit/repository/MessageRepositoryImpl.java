package com.sprint.mission.discodeit.repository;

import static com.sprint.mission.discodeit.entity.binarycontent.QBinaryContent.binaryContent;
import static com.sprint.mission.discodeit.entity.channel.QChannel.channel;
import static com.sprint.mission.discodeit.entity.message.QMessage.message;
import static com.sprint.mission.discodeit.entity.status.user.QUserStatus.userStatus;
import static com.sprint.mission.discodeit.entity.user.QUser.user;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.discodeit.entity.binarycontent.QBinaryContent;
import com.sprint.mission.discodeit.entity.message.Message;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageQuerydslRepository {

  private final JPAQueryFactory queryFactory;

  private final QBinaryContent attachmentContent = new QBinaryContent("attachmentContent");

  @Override
  public Slice<Message> cursorBasedPaging(UUID channelId, Instant cursor, Pageable slice) {

    List<Message> messages = queryFactory
        .select(message)
        .from(message)
        .join(message.channel, channel).fetchJoin()
        .join(message.author, user).fetchJoin()
        .leftJoin(user.profile, binaryContent).fetchJoin()
        .join(user.status, userStatus).fetchJoin()
        .leftJoin(message.attachments, attachmentContent).fetchJoin()
        .where(
            message.channel.id.eq(UUID.fromString(channelId.toString())),
            cursor(cursor, slice)
        )
        .orderBy(
            orderByField(slice)
        )
        .limit(slice.getPageSize() + 1)
        .fetch();

    boolean hasNext = messages.size() > getSize(slice);

    return new SliceImpl<>(messages, slice, hasNext);
  }

  @Override
  public Long totalCount(UUID channelId, Instant cursor, Pageable slice) {
    return queryFactory
        .select(message.count())
        .from(message)
        .join(message.channel, channel)
        .join(message.author, user)
        .leftJoin(user.profile, binaryContent)
        .join(user.status, userStatus)
        .leftJoin(message.attachments, attachmentContent)
        .where(
            message.channel.id.eq(channelId)
        )
        .fetchOne();
  }

  private int getSize(Pageable slice) {
    return slice.getPageSize() == 0 ? 10 : slice.getPageSize();
  }

  private BooleanExpression cursor(Instant cursor, Pageable slice) {
    if (cursor == null) {
      return null;
    }

    Sort sort = slice.getSort();
    for (Order order : sort) {
      Direction direction = order.getDirection();

      if (direction.isDescending()) {
        return message.createdAt.lt(cursor);
      } else {
        return message.createdAt.gt(cursor);
      }
    }

    return null;
  }

  private OrderSpecifier<?> orderByField(Pageable slice) {
    Sort sort = slice.getSort();

    for (Order order : sort) {
      String property = order.getProperty();
      Direction direction = order.getDirection();

      if (property.equals("createdAt")) {
        if (direction.isDescending()) {
          return message.createdAt.desc();
        } else {
          return message.createdAt.asc();
        }
      }
    }

    return null;
  }

}
