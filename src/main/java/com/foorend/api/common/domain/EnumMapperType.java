package com.foorend.api.common.domain;

import java.io.Serializable;

public interface EnumMapperType extends Serializable {
    int getCode();
    String getName();
    String getTitle();
}

