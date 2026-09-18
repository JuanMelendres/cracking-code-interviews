package demo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/** The constraint annotations below are read directly by springdoc/swagger
 * to build the real generated schema -- @Min(1) becomes a real "minimum": 1
 * in the spec, @NotBlank becomes a real "required" entry, with no
 * hand-written OpenAPI YAML/JSON anywhere in this pack. The spec cannot
 * drift from these annotations because it IS generated from them. */
public class CreateOrderRequest {

    @NotBlank
    @Schema(description = "Customer's full name", example = "Ada Lovelace")
    public String customerName;

    @Min(1)
    @Schema(description = "Order amount, in USD cents", example = "1999")
    public long amountCents;
}
