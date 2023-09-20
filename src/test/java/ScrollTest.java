import core.appium_support.scroll.Scroll;
import core.appium_support.scroll.Toward;
import org.junit.jupiter.api.Test;

public class ScrollTest {

    @Test
    public void testScroll() {
        new Scroll(null).scrollVertical(null, 10, 1, 1, Toward.RIGHT);
    }

}
