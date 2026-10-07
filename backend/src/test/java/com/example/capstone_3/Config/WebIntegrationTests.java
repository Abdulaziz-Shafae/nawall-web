package com.example.capstone_3.Config;

import com.example.capstone_3.Model.*;
import com.example.capstone_3.Repository.AccountRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebIntegrationTests {
    private final AccountRepository accounts = mock(AccountRepository.class);
    private final EntityManager entities = mock(EntityManager.class);
    private HandlerInterceptor interceptor;
    @BeforeEach void setup() {
        WebIntegrationConfig config = new WebIntegrationConfig(accounts, entities);
        ReflectionTestUtils.setField(config, "origins", "http://localhost:5173");
        InterceptorRegistry registry = mock(InterceptorRegistry.class);
        when(registry.addInterceptor(any())).thenReturn(mock(InterceptorRegistration.class));
        config.addInterceptors(registry);
        ArgumentCaptor<HandlerInterceptor> captor = ArgumentCaptor.forClass(HandlerInterceptor.class);
        verify(registry).addInterceptor(captor.capture()); interceptor = captor.getValue();
    }
    private MockHttpServletRequest req(String method, String path, String role) {
        MockHttpServletRequest req = new MockHttpServletRequest(method, path);
        req.addHeader("X-Requested-With", "XMLHttpRequest");
        if (role != null) { Account account = new Account(); account.setId(1); account.setAccountType(role); req.getSession().setAttribute("accountId",1); when(accounts.findAccountById(1)).thenReturn(account); }
        return req;
    }
    @Test void guestsCanDiscoverButCannotReadAccounts() throws Exception {
        assertTrue(interceptor.preHandle(req("GET","/api/v1/skill/get",null),new MockHttpServletResponse(),null));
        MockHttpServletResponse res = new MockHttpServletResponse();
        assertFalse(interceptor.preHandle(req("GET","/api/v1/account/get",null),res,null)); assertEquals(401,res.getStatus());
    }
    @Test void membersCannotChangeRolesOrForgeSkillVerification() throws Exception {
        for (String path : new String[]{"/api/v1/account/update/1","/api/v1/account-skill/update/1","/api/v1/skill-assessment/take/1"}) {
            MockHttpServletResponse res=new MockHttpServletResponse(); assertFalse(interceptor.preHandle(req("PUT",path,"INDIVIDUAL"),res,null));assertEquals(403,res.getStatus());
        }
    }
    @Test void adminsCanReadMaintenanceRoutes() throws Exception {
        assertTrue(interceptor.preHandle(req("GET","/api/v1/account/get","ADMIN"),new MockHttpServletResponse(),null));
    }
    @Test void rawOwnerUpdatesRequireActualOfferOwnership() throws Exception {
        Account owner=new Account();owner.setId(1);SkillOffer offer=new SkillOffer();offer.setProviderAccount(owner);when(entities.find(SkillOffer.class,8)).thenReturn(offer);
        assertTrue(interceptor.preHandle(req("PUT","/api/v1/skill-offer/update/8","INDIVIDUAL"),new MockHttpServletResponse(),null));
        owner.setId(2);MockHttpServletResponse res=new MockHttpServletResponse();assertFalse(interceptor.preHandle(req("DELETE","/api/v1/skill-offer/delete/8","INDIVIDUAL"),res,null));assertEquals(403,res.getStatus());
    }
    @Test void crossSiteMutationsAndSimpleFormsAreRejected() throws Exception {
        MockHttpServletRequest req=req("POST","/api/v1/account/login",null);req.addHeader("Origin","https://untrusted.example");
        assertFalse(interceptor.preHandle(req,new MockHttpServletResponse(),null));
        req=new MockHttpServletRequest("POST","/api/v1/account/login");assertFalse(interceptor.preHandle(req,new MockHttpServletResponse(),null));
    }
    @Test void sharedKeyProfilesCannotBeMovedByMembers() throws Exception {
        assertTrue(interceptor.preHandle(req("PUT","/api/v1/individual-profile/update/1","INDIVIDUAL"),new MockHttpServletResponse(),null));
        assertFalse(interceptor.preHandle(req("PUT","/api/v1/individual-profile/update/2","INDIVIDUAL"),new MockHttpServletResponse(),null));
        assertFalse(interceptor.preHandle(req("DELETE","/api/v1/individual-profile/delete/1","INDIVIDUAL"),new MockHttpServletResponse(),null));
    }
    @Test void healthAndRegistrationImagesArePublicButUploadsRequireTrustedOrigins() throws Exception {
        assertTrue(interceptor.preHandle(req("GET","/api/v1/health",null),new MockHttpServletResponse(),null));
        assertTrue(interceptor.preHandle(req("GET","/api/v1/media/images/01234567-1234-1234-1234-012345678901.png",null),new MockHttpServletResponse(),null));
        assertTrue(interceptor.preHandle(req("POST","/api/v1/media/images",null),new MockHttpServletResponse(),null));
        var untrusted=req("POST","/api/v1/media/images",null);untrusted.addHeader("Origin","https://untrusted.example");
        assertFalse(interceptor.preHandle(untrusted,new MockHttpServletResponse(),null));
    }
}
