package com.pkm.medicalinventory.entity;
//package com.pkm.medicalinventory.entity;
//
//import static com.pkm.medicalinventory.util.EntityUtil.getDeclaredField;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.ParameterizedType;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Objects;
//
//import com.pkm.medicalinventory.annotation.CustomEntity;
//import com.pkm.medicalinventory.annotation.FormField;
//import com.pkm.medicalinventory.dto.model.BaseModel;
//import com.pkm.medicalinventory.util.EntityUtil;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class ModelConverter<M extends BaseModel> {
//	
//	final BaseEntity object;
//	
//	public ModelConverter(BaseEntity object) {
//		this.object = object;
//	}
//	
//	private boolean isListOf(Field field, Class superClass) {
//		Class listArg = getListArgument(field);
//		return listArg != null && superClass.equals(listArg.getSuperclass());
//	}
//	
//	private Class getListArgument(Field field) {
//		Class<?> fieldType = field.getType();
//		if (0 == fieldType.getInterfaces().length)
//			return null;
////		log.info("Field: {} | interface: {}", field.getName(), field.getType().getInterfaces());
//		if (fieldType.equals(List.class)
//				|| field.getGenericType() != null && Arrays.asList(fieldType.getInterfaces()).contains(List.class)) {
//
//			java.lang.reflect.Type t = ((java.lang.reflect.Type) fieldType);
//			if (!(field.getGenericType() instanceof ParameterizedType)) {
//				return null;
//			}
//			java.lang.reflect.Type[] argument = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
//			if (argument != null && argument.length > 0 && argument[0] instanceof Class) {
////				log.info("type argument: {}", argument);
//				return (Class) argument[0];
//			}
//		}
//
//		return null;
//	}
//
//	
//	/**
//	 * copy field having supperClass' type of baseEntity
//	 * 
//	 * @param e
//	 * @throws Exception
//	 */
//	protected final void setObjectModel(M model) throws Exception {
//		Class<M> modelClass = (Class<M>) model.getClass();
//		Objects.requireNonNull(model);
//		List<Field> fields = getObjectModelField();
//		for (Field field : fields) {
//			Object value = field.get(object);
//			if (null == value)
//				continue;
//			Field modelField = getDeclaredField(modelClass, field.getName());
//
//			if (null == modelField) {
//				continue;
//			}
//			Object finalValue = null;
//			Class listArgument = getListArgument(modelField);
//			Class entityListArg = getListArgument(field);
//			if (value instanceof BaseEntity && ((BaseEntity) value).isConvertedToModel() == false) {
//				finalValue = ((BaseEntity) value).toModel();
//			} else if (value!=null && value instanceof List && 
//					isListOf(field, BaseEntity.class) &&
//					isListOf(modelField, BaseModel.class)) {
//				final List listVal = new LinkedList<>();
//				((List<BaseEntity>) value).forEach(v->{
//					if (v.isConvertedToModel() == false) {
//						BaseModel item =  v.toModel();
//						if (item != null) {
//							listVal.add(item);
//						}
//					}
//				});
//				if (listVal.size() > 0) {
//					finalValue = listVal;
//				}
//				
//			}
////			log.info("SetObjectModel {} = {}", modelField.getName(), finalValue);
////			log.info("ID: {}", object.getId());
//			modelField.set(model, finalValue);
//		}
//		setFieldValuesHavingEntityFieldProp(model);
//	}
//	
//	protected final List<Field> getObjectModelField() {
////		log.info("===== model: {}", object.getClass());
//		List<Field> fields = EntityUtil.getDeclaredFields(object.getClass());
//		List<Field> filtered = new ArrayList<>();
//		for (Field field : fields) {
//			final Class<?> fieldType = field.getType();
//			if (BaseEntity.class.equals(fieldType.getSuperclass())){
//				filtered.add(field);
//				continue;
//			}
//
//			if (isListOf(field, BaseEntity.class)) {
//				filtered.add(field);
//				continue;
//			}
//		}
//
//		return filtered;
//	}
//
//	private void setFieldValuesHavingEntityFieldProp(M model) throws IllegalArgumentException, IllegalAccessException {
//		List<Field> modelFields = EntityUtil.getDeclaredFields(model.getClass());
//		for (Field modelField : modelFields) {
//
//			FormField formField = modelField.getAnnotation(FormField.class);
//			if (!BaseModel.class.equals(modelField.getType().getSuperclass()) || null == formField)
//				continue;
//			if (!formField.entityField().trim().isEmpty()) {
//
//				Field entityField = getDeclaredField(object.getClass(), formField.entityField().trim());
//				if (null != entityField && BaseEntity.class.equals(entityField.getType().getSuperclass())) {
//					Object value = entityField.get(object);
//					if (null != value) {
//						System.out.println("XXXXXXXXXXXXXXXX: "+modelField.getName());
//						log.info("XXXXXXXvalue: {}", entityField.getName());
//						if (value instanceof Transaction) {
//							Transaction tx = (Transaction) value;
//							System.out.println(tx.getProductFlows().get(0).getTransaction());
//							
//						}
////						BaseModel finalValue = ((BaseEntity) value).toModel();
////						modelField.setAccessible(true);
////						modelField.set(model, finalValue);
//					}
//				}
//			}
//		}
//
//	}
//	
//	protected M newModelInstance() throws Exception {
//		CustomEntity customEntity = object.getClass().getAnnotation(CustomEntity.class);
//		Objects.requireNonNull(customEntity);
//		Class<? extends M> entityClass = object.getTypeArgument();
//		M instance = (M) entityClass.newInstance();
//		return instance;
//	}
//	
//	public String[] notNullFieldNames(M instance) {
//		List<Field> fields = EntityUtil.getDeclaredFields(instance.getClass());
//		List<String > notNullNames = new ArrayList<>();
//		for (Field field : fields) {
//			if (field.getType().isPrimitive()) {
//				continue;
//			}
//			try {
//				if (field.get(instance)!=null) {
//					notNullNames.add(field.getName());
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//		}
//		return notNullNames.toArray(new String[] {});
//	}
//
//}
