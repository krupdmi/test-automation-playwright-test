package org.testautomation.commons.utils.usercredentials;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Locale;

/**
 * Thin abstraction over JavaFaker for generating realistic test data.
 * Keeping Faker behind this facade means swapping the underlying library
 * only requires changes here.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FakeDataHelper {

    private static final Faker FAKER = new Faker(Locale.ENGLISH);

    public static String firstName()    { return FAKER.name().firstName(); }
    public static String lastName()     { return FAKER.name().lastName(); }
    public static String fullName()     { return FAKER.name().fullName(); }
    public static String email()        { return FAKER.internet().emailAddress(); }
    public static String username()     { return FAKER.name().username(); }
    public static String password()     { return FAKER.internet().password(10, 20, true, true); }
    public static String phone()        { return FAKER.phoneNumber().phoneNumber(); }
    public static String address()      { return FAKER.address().streetAddress(); }
    public static String city()         { return FAKER.address().city(); }
    public static String country()      { return FAKER.address().country(); }
    public static String uuid()         { return FAKER.internet().uuid(); }
    public static String alphanumeric(int length) { return FAKER.regexify("[a-zA-Z0-9]{" + length + "}"); }
    public static int numberBetween(int min, int max) { return FAKER.number().numberBetween(min, max); }
}
