package playwrightLLM;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.SelectOption;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class PlayWrightLLM {
	private Playwright playwright;
	private Browser browser;
	private Page page;

	@BeforeEach
	void setUp() {
		playwright = Playwright.create();
		browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
		page = browser.newPage();
	}

	@AfterEach
	void tearDown() {
		if (page != null) page.close();
		if (browser != null) browser.close();
		if (playwright != null) playwright.close();
	}

	@Test
	public void searchFilterAddToCart_verifyCartShowsOneItem() {
		page.navigate("https://depaul.bncollege.com/");

		// 1) Search for 'earbuds'
		// First click the search (magnifying glass) to reveal the search input
		tryClickAny(page, new String[]{"button[aria-label*='Search']", "button.search-toggle", "button[title*='Search']", ".icon-search", ".search-toggle", "button[aria-label='Search']"});

		// Wait for a visible search input to appear and then use it
		String[] searchInputs = new String[]{"input[type='search']", "input[placeholder*='Search']", "input[aria-label*='Search']", "input[name='q']", "input[name='search']"};
		Locator search = null;
		for (String sel : searchInputs) {
			try {
				page.waitForSelector(sel, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
				search = page.locator(sel);
				break;
			} catch (PlaywrightException ignored) {
			}
		}
		if (search == null) search = findFirst(page, searchInputs);
		try {
			search.fill("earbuds");
			search.press("Enter");
		} catch (PlaywrightException e) {
			// As a fallback try focusing then typing
			try {
				search.click(new Locator.ClickOptions().setTimeout(3000));
				search.fill("earbuds");
				search.press("Enter");
			} catch (PlaywrightException ignored) {
			}
		}
		page.waitForLoadState();

		// Wait for search results container to appear before applying filters
		String[] resultContainers = new String[]{".search-results", ".product-grid", ".product-list", "#searchResults", ".products", ".searchView", "ul.productList", "div.search-results", ".search-results-list"};
		boolean resultsVisible = false;
		for (String rc : resultContainers) {
			try {
				page.waitForSelector(rc, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
				resultsVisible = true;
				break;
			} catch (PlaywrightException ignored) {
			}
		}
		if (!resultsVisible) {
			try {
				Files.createDirectories(Paths.get("target/playwright-debug"));
				Files.writeString(Paths.get("target/playwright-debug/search-failure.html"), page.content(), StandardCharsets.UTF_8);
				page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("target/playwright-debug/search-failure.png")).setFullPage(true));
			} catch (Exception ignored) {
			}
		}

		// 2) Apply filters: Brand 'JBL', Color 'Black', Price 'Over $50'
		tryClickByText(page, new String[]{"JBL", "Brand: JBL", "Brand JBL"});
		tryClickByText(page, new String[]{"Black", "Color: Black", "Color Black"});
		tryClickByText(page, new String[]{"Over $50", "Over $ 50", "> $50", "$50+"});

		// Wait for filter results to update
		page.waitForLoadState();

		// 3) Click the first product in the results — try multiple strategies
		boolean clickedProduct = false;
		String[] productSelectors = new String[]{".product a", ".product-item a", ".search-result a", "a.product-link", "a[href*='/product/']", "a[href*='/p/']", "a[href*='/dp/']", "article a", "main a", "a:has(img)", "a:has(.product-image)", "a:has(.thumb)", "a[href*='sku']"};
		for (String sel : productSelectors) {
			try {
				page.waitForSelector(sel, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
				Locator cand = page.locator(sel).filter(new Locator.FilterOptions());
				if (cand.count() > 0) {
					cand.first().click(new Locator.ClickOptions().setTimeout(15000));
					page.waitForLoadState();
					clickedProduct = true;
					break;
				}
			} catch (PlaywrightException ignored) {
			}
		}
		if (!clickedProduct) {
			// Fallback: click the first anchor with a product-like href
			try {
				Locator fallback = page.locator("a[href*='/product/'], a[href*='/p/'], a[href*='/dp/'], a[href*='/s/']");
				if (fallback.count() > 0) {
					fallback.first().click(new Locator.ClickOptions().setTimeout(15000));
					page.waitForLoadState();
					clickedProduct = true;
				}
			} catch (PlaywrightException ignored) {
			}
		}
		if (!clickedProduct) {
			// save debug artifacts
			try {
				Files.createDirectories(Paths.get("target/playwright-debug"));
				Files.writeString(Paths.get("target/playwright-debug/product-failure.html"), page.content(), StandardCharsets.UTF_8);
				page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("target/playwright-debug/product-failure.png")).setFullPage(true));
			} catch (Exception ignored) {
			}
			throw new PlaywrightException("Unable to find/click a product link on the results page. Debug saved to target/playwright-debug/");
		}

		// 4) Add to cart
		// If product has options (size/color), try selecting the first available option
		try {
			Locator selects = page.locator("select");
			if (selects.count() > 0) {
				for (int i = 0; i < selects.count(); ++i) {
					Locator s = selects.nth(i);
					try {
						String val = s.inputValue();
						// select first non-empty option by index 1
						s.selectOption(new SelectOption().setIndex(1));
					} catch (PlaywrightException ignored) {
					}
				}
			}
		} catch (PlaywrightException ignored) {
		}

		// Also try radio/button option groups
		try {
			Locator radios = page.locator("input[type='radio']");
			if (radios.count() > 0) radios.first().check();
		} catch (PlaywrightException ignored) {}

		boolean added = tryClickAny(page, new String[]{"text=Add to Cart", "text=Add to cart", "text=Add to bag", "text=Add to Basket", "button:has-text('Add to Cart')", "button:has-text('Add to Bag')", "button:has-text('Add')", "button.add-to-cart", "button[name='add']", "button#add-to-cart", "input[type='submit'][value*='Add']"});
		if (!added) {
			// attempt to click any button with aria-label or title
			added = tryClickAny(page, new String[]{"button[aria-label*='Add']", "button[title*='Add']", ".add-to-cart", ".btn-add"});
		}

		// Wait briefly for cart update or confirmation message (check multiple selectors)
		try {
			page.waitForTimeout(2000);
			String[] confirmationSelectors = new String[]{"text=added", "text=Added", ".mini-cart", ".cart-count", ".added-to-cart"};
			for (String cs : confirmationSelectors) {
				try {
					page.waitForSelector(cs, new Page.WaitForSelectorOptions().setTimeout(8000));
					break;
				} catch (PlaywrightException ignored) {
				}
			}
		} catch (PlaywrightException ignored) {
		}

		// 5) Open cart and verify 1 item
		boolean openedCart = tryClickAny(page, new String[]{"text=Cart", "text=View cart", "text=My Cart", "a[href*='cart']", "a[aria-label='Cart']", "a[title*='Cart']", "a[href*='/cart']"});
		if (!openedCart) {
			page.navigate("https://depaul.bncollege.com/cart");
		}
		page.waitForLoadState();

		int items = countCartItems(page);
		assertEquals(1, items, "Cart should contain 1 item");
	}

	// Helpers
	private Locator findFirst(Page page, String[] selectors) {
		for (String sel : selectors) {
			try {
				Locator l = page.locator(sel);
				if (l.count() > 0) return l;
			} catch (PlaywrightException ignored) {
			}
		}
		return page.locator(selectors[0]);
	}

	private void tryClickByText(Page page, String[] texts) {
		for (String t : texts) {
			try {
				Locator l = page.locator("text=\"" + t + "\"");
				if (l.count() > 0) {
					l.first().click(new Locator.ClickOptions().setTimeout(3000));
					page.waitForLoadState();
					return;
				}
			} catch (PlaywrightException ignored) {
			}
		}
	}

	private boolean tryClickAny(Page page, String[] selectors) {
		for (String sel : selectors) {
			try {
				Locator l = page.locator(sel);
				if (l.count() > 0) {
					l.first().click(new Locator.ClickOptions().setTimeout(5000));
					page.waitForLoadState();
					return true;
				}
			} catch (PlaywrightException ignored) {
			}
		}
		return false;
	}

	private int countCartItems(Page page) {
		// Try common cart item selectors
		String[] cartItemSelectors = new String[]{".cart-item", ".cart__item", ".basket-item", ".line-item", "tr.cart-row", "li.cart-item"};
		for (String sel : cartItemSelectors) {
			try {
				Locator l = page.locator(sel);
				int c = l.count();
				if (c > 0) return c;
			} catch (PlaywrightException ignored) {
			}
		}

		// Fallback: try to find quantity inputs and sum their values
		try {
			Locator qtyInputs = page.locator("input[type='number']");
			if (qtyInputs.count() > 0) {
				int total = 0;
				for (int i = 0; i < qtyInputs.count(); ++i) {
					String val = qtyInputs.nth(i).getAttribute("value");
					if (val == null || val.isEmpty()) val = qtyInputs.nth(i).inputValue();
					try { total += Integer.parseInt(val); } catch (NumberFormatException ignored) {}
				}
				if (total > 0) return total;
			}
		} catch (PlaywrightException ignored) {
		}

		// As last resort, look for a cart badge count
		try {
			Locator badge = page.locator(".cart-count, .count, .cart-badge, .mini-cart-count");
			if (badge.count() > 0) {
				String text = badge.first().textContent().replaceAll("[^0-9]", "");
				if (!text.isEmpty()) return Integer.parseInt(text);
			}
		} catch (PlaywrightException ignored) {
		}

		return 0;
	}
}

