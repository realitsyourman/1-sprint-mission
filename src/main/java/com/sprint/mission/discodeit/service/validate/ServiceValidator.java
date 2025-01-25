package com.sprint.mission.discodeit.service.validate;

public interface ServiceValidator<T> {

    /**
     *
     * @param t
     * @return T
     * @Description: User, Message, Channel 객체를 받아 null인지 검증
     */
    T entityValidate(T t);

    default boolean isNullParam(String... str) {
        for (String s : str) {
            if (s == null) {
                return true;
            }
        }

        return false;
    }

    //boolean isNullParam(String param, T t);
}
