package com.pkm.medicalinventory.management;

import java.util.List;

import com.pkm.medicalinventory.entity.BaseEntity;

public interface EntityUpdateService<T extends BaseEntity> {

	public T saveEntity(T object, boolean newRecord);

	public void postFilter(List<T> objects);

	public T deleteEntity(Long id, Class<T> _class);

}
