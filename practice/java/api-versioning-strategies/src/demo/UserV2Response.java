package demo;

/** V2 shape: the real breaking change -- "name" split into "firstName"/"lastName". */
public record UserV2Response(long id, String firstName, String lastName) {
}
