package com.alpha.gateway.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {

    @Test
    void testValidate() {

        final boolean valid = JwtUtils.isValid("bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.k4DFETucHbH3ClcTt4NoiODIX_bjzcQtA_vt7xNbygg");
        assertThat(valid).isTrue();
    }

    @Test
    void testNotValidToken() {
        final boolean valid = JwtUtils.isValid("bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjm0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.k4DFETucHbH3ClcTt4NoiODIX_bjzcQtA_vt7xNbygg");
        assertThat(valid).isFalse();
    }
}