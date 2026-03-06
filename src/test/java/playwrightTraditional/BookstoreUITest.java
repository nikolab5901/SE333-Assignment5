package playwrightTraditional;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BookstoreUITest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

        // Video recording setup
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("videos/"))
                .setRecordVideoSize(1280, 720));

        page = context.newPage();
    }

    @AfterEach
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }

    @Test
    @DisplayName("DePaul Bookstore UI Full Purchase Pathway")
    void testBookstorePurchasePathway() {

        page.navigate("https://depaul.bncollege.com/link");
        page.locator("#bned_site_search").click();
        page.locator("#bned_site_search").fill("earbuds");
        page.locator("#bned_site_search").press("Enter");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("brand")).click();
        page.locator("#facet-brand").getByRole(AriaRole.LIST).locator("label").filter(new Locator.FilterOptions().setHasText("brand JBL")).getByRole(AriaRole.IMG).click();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Color")).click();
        page.locator("label").filter(new Locator.FilterOptions().setHasText("Color Black")).getByRole(AriaRole.IMG).click();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price")).click();
        page.locator("#facet-price").getByRole(AriaRole.IMG).click();


        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("JBL Quantum True Wireless")).click();

        page.waitForTimeout(3000);

        assertThat(page.locator("body")).containsText("JBL Quantum");
        assertThat(page.locator("body")).containsText("164.98");

        // Check the raw HTML to bypass collapsed "Product Details" accordions
        Assertions.assertTrue(page.content().contains("SKU") || page.content().contains("Item"), "Could not find SKU/Item number");
        Assertions.assertTrue(page.content().contains("Noise Cancelling"), "Could not find product description");
        page.getByLabel("Add to cart").click();

        Locator cartLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Cart 1 items"));
        assertThat(cartLink).isVisible();
        cartLink.click();

        assertThat(page.locator("body")).containsText("Your Shopping Cart");
        assertThat(page.locator("body")).containsText("$164.98");

        page.getByText("FAST In-Store Pickup").first().click();

        assertThat(page.locator("body")).containsText("164.98"); // Base price
        assertThat(page.locator("body")).containsText("3.00");   // New handling fee
        assertThat(page.locator("body")).containsText("TBD");    // Taxes still TBD
        assertThat(page.locator("body")).containsText("167.98"); // New estimated total (164.98 + 3.00)

        page.getByLabel("Enter Promo Code").click();
        page.getByLabel("Enter Promo Code").fill("TEST");
        page.getByLabel("Apply Promo Code").click();

        page.waitForTimeout(2000);

        assertThat(page.locator("body")).containsText("valid");
        
        page.getByLabel("Proceed To Checkout").click();


        assertThat(page.locator("body")).containsText("Create Account");
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Proceed As Guest")).click();

        assertThat(page.locator("body")).containsText("Contact Information");

        page.getByPlaceholder("Please enter your first name").click();
        page.getByPlaceholder("Please enter your first name").fill("Nick");
        page.getByPlaceholder("Please enter your last name").click();
        page.getByPlaceholder("Please enter your last name").fill("Nick");
        page.getByPlaceholder("Please enter a valid email").click();
        page.getByPlaceholder("Please enter a valid email").fill("nikolab5901@gmail.com");
        page.getByPlaceholder("Please enter a valid phone").click();
        page.getByPlaceholder("Please enter a valid phone").fill("3312139756");

        assertThat(page.locator("body")).containsText("164.98"); // Base price
        assertThat(page.locator("body")).containsText("3.00");   // New handling fee
        assertThat(page.locator("body")).containsText("TBD");    // Taxes still TBD
        assertThat(page.locator("body")).containsText("167.98"); // New estimated total (164.98 + 3.00)

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();

        page.waitForTimeout(2000);

        assertThat(page.locator("body")).containsText("Nick");
        assertThat(page.locator("body")).containsText("nikolab5901@gmail.com");
        assertThat(page.locator("body")).containsText("3312139756");
        assertThat(page.locator("body")).containsText("DePaul University"); // Validating loop campus
        assertThat(page.locator("body")).containsText("I'll pick them up");

        assertThat(page.locator("body")).containsText("164.98"); // Base price
        assertThat(page.locator("body")).containsText("3.00");   // New handling fee
        assertThat(page.locator("body")).containsText("TBD");    // Taxes still TBD
        assertThat(page.locator("body")).containsText("167.98"); // New estimated total (164.98 + 3.00)
        assertThat(page.locator("body")).containsText("JBL Quantum True Wireless");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();

        page.waitForTimeout(2000);

        assertThat(page.locator("body")).containsText("164.98"); // Base price
        assertThat(page.locator("body")).containsText("3.00");   // Handling fee
        assertThat(page.locator("body")).containsText("17.22");  // Exact tax
        assertThat(page.locator("body")).containsText("185.20"); // Final total
        assertThat(page.locator("body")).containsText("JBL Quantum True Wireless");

        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Back to cart")).click();

        page.getByLabel("Remove product JBL Quantum").click();

        assertThat(page.locator("body")).containsText("empty");

    }
}