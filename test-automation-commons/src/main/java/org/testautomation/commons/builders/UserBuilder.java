package org.testautomation.commons.builders;

import lombok.Getter;
import org.testautomation.commons.utils.usercredentials.FakeDataHelper;

/**
 * Fluent builder for assembling a User test-data object.
 * Pre-populated with random fake data — override only the fields your test cares about.
 *
 * <p>Usage:
 * <pre>
 *   UserBuilder user = UserBuilder.random()
 *       .withEmail("specific@example.com")
 *       .withStatus("ACTIVE");
 * </pre>
 */
@Getter
public class UserBuilder {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String status;

    private UserBuilder() {}

    /** Creates a builder pre-filled with random fake data. */
    public static UserBuilder random() {
        return new UserBuilder()
                .withUsername(FakeDataHelper.username())
                .withEmail(FakeDataHelper.email())
                .withFirstName(FakeDataHelper.firstName())
                .withLastName(FakeDataHelper.lastName())
                .withPassword(FakeDataHelper.password())
                .withStatus("ACTIVE");
    }

    /** Creates an empty builder — all fields must be set explicitly. */
    public static UserBuilder empty() {
        return new UserBuilder();
    }

    public UserBuilder withUsername(String username)   { this.username  = username;  return this; }
    public UserBuilder withEmail(String email)         { this.email     = email;     return this; }
    public UserBuilder withFirstName(String firstName) { this.firstName = firstName; return this; }
    public UserBuilder withLastName(String lastName)   { this.lastName  = lastName;  return this; }
    public UserBuilder withPassword(String password)   { this.password  = password;  return this; }
    public UserBuilder withStatus(String status)       { this.status    = status;    return this; }
}
