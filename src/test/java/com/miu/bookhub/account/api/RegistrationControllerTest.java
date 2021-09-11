package com.miu.bookhub.account.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miu.bookhub.account.api.domain.UserRequest;
import com.miu.bookhub.account.repository.entity.Role;
import com.miu.bookhub.account.repository.entity.User;
import com.miu.bookhub.account.service.RegistrationService;
import com.miu.bookhub.TestConfig;
import com.miu.bookhub.global.GlobalConfig;
import com.miu.bookhub.global.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Import({TestConfig.class, GlobalConfig.class, SecurityConfig.class})
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = RegistrationController.class)
public class RegistrationControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean private RegistrationService registrationService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation).operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldRegisterNewCustomer() throws Exception {

        UserRequest request = UserRequest.builder()
                .firstName("Abel")
                .lastName("Adam")
                .emailAddress("abel.adam@email.com")
                .password("Password1")
                .build();

        when(registrationService.registerCustomer(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(buildMockUser());

        mockMvc.perform(post("/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andDo(document("register-customer",
                        requestFields(
                                fieldWithPath("firstName").description("Given name of the new account"),
                                fieldWithPath("lastName").description("Surname of the new account"),
                                fieldWithPath("emailAddress").description("Email address of the new account"),
                                fieldWithPath("password").description("Password for the new account")
                        ),
                        getUserResponseFields()
                ));
    }

    @Test
    void shouldFindUserById() throws Exception {

        when(registrationService.findUserById(anyLong()))
                .thenReturn(Optional.of(buildMockUser()));

        mockMvc.perform(get("/users/{userId}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("find-user-by-id", getUserResponseFields()));
    }

    @Test
    void shouldFindUserByEmailAddress() throws Exception {

        when(registrationService.findUserByEmail(anyString()))
                .thenReturn(Optional.of(buildMockUser()));

        mockMvc.perform(get("/users?email=abel.adam@email.com"))
                .andExpect(status().isOk())
                .andDo(document("find-user-by-email", getUserResponseFields()));
    }

    @WithMockUser
    @Test
    void shouldMakeAccountSeller() throws Exception {

        when(registrationService.upgradeAccountToSeller(anyLong()))
                .thenReturn(buildMockUser());

        mockMvc.perform(put("/users/{userId}/roles/seller", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("make-account-seller", getUserResponseFields()));
    }

    @WithMockUser
    @Test
    void shouldLockAccount() throws Exception {

        mockMvc.perform(post("/users/{userId}/lock", 1L))
                .andExpect(status().isOk())
                .andDo(document("lock-account"));
    }

    @WithMockUser
    @Test
    void shouldDisableAccount() throws Exception {

        mockMvc.perform(delete("/users/{userId}/", 1L))
                .andExpect(status().isOk())
                .andDo(document("disable-account"));
    }

    private User buildMockUser() {
        return User.builder()
                .id(1L)
                .firstName("Abel")
                .lastName("Adam")
                .emailAddress("abel.adam@email.com")
                .isActive(true)
                .isLocked(false)
                .roles(Set.of(Role.CUSTOMER))
                .build();
    }

    private ResponseFieldsSnippet getUserResponseFields() {
        return responseFields(
                fieldWithPath("userId").description("Unique ID for the newly registered account"),
                fieldWithPath("firstName").description("First name of the newly registered account"),
                fieldWithPath("lastName").description("Last name of the newly registered account"),
                fieldWithPath("emailAddress").description("Email address of the newly registered account"),
                subsectionWithPath("roles").description("Roles assigned to the newly registered account")
        );
    }
}
