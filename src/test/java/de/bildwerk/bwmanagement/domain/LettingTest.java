package de.bildwerk.bwmanagement.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import de.bildwerk.bwmanagement.web.rest.TestUtil;

public class LettingTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Letting.class);
        Letting letting1 = new Letting();
        letting1.setId(1L);
        Letting letting2 = new Letting();
        letting2.setId(letting1.getId());
        assertThat(letting1).isEqualTo(letting2);
        letting2.setId(2L);
        assertThat(letting1).isNotEqualTo(letting2);
        letting1.setId(null);
        assertThat(letting1).isNotEqualTo(letting2);
    }
}
