package demo;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** A real MapStruct mapper -- MapStruct's own annotation processor
 * generates a real {@code OrderMapperImpl} class at compile time (see
 * this pack's own README for the exact generated source captured from a
 * real build). {@code componentModel = "spring"} makes the generated
 * implementation a real {@code @Component}, injectable into a Spring
 * service exactly like any other bean -- no manual wiring. */
@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "customerName", source = "customer.name")
    OrderResponse toResponse(OrderEntity entity);
}
