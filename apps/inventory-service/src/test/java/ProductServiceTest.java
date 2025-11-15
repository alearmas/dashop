import com.aarmas.inventory_service.dtos.ProductPatchRequest;
import com.aarmas.inventory_service.dtos.ProductRequest;
import com.aarmas.inventory_service.exceptions.ProductNotFoundException;
import com.aarmas.inventory_service.models.ContentUnit;
import com.aarmas.inventory_service.models.Product;
import com.aarmas.inventory_service.models.ProductType;
import com.aarmas.inventory_service.models.Size;
import com.aarmas.inventory_service.repositories.ProductRepository;
import com.aarmas.inventory_service.services.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @Test
    @DisplayName("getAll() should return the list of products provided by the repository")
    void getAll_shouldReturnListFromRepository() {
        // given
        var p1 = new Product();
        var p2 = new Product();
        when(repository.getAll()).thenReturn(List.of(p1, p2));

        // when
        List<Product> result = service.getAll();

        // then
        assertThat(result).containsExactly(p1, p2);
        verify(repository).getAll();
    }

    @Test
    @DisplayName("getByBrand() should trim the brand and delegate to repository")
    void getByBrand_shouldTrimBrandAndDelegate() {
        // given
        var p = new Product();
        when(repository.getByBrand("Pampers")).thenReturn(List.of(p));

        // when
        List<Product> result = service.getByBrand("  Pampers  ");

        // then
        assertThat(result).containsExactly(p);
        verify(repository).getByBrand("Pampers");
    }

    @Test
    @DisplayName("get() should return the product when it exists")
    void get_shouldReturnProductWhenExists() {
        // given
        var product = new Product();
        product.setProductID("123");
        when(repository.findById("123")).thenReturn(Optional.of(product));

        // when
        Product result = service.get("123");

        // then
        assertThat(result).isSameAs(product);
        verify(repository).findById("123");
    }

    @Test
    @DisplayName("get() should throw ProductNotFoundException when product does not exist")
    void get_shouldThrowProductNotFoundWhenNotExists() {
        // given
        when(repository.findById("no-existe")).thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> service.get("no-existe"))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("no-existe");
    }

    @Test
    @DisplayName("create() should build a Product from the request, assign a UUID and save it")
    void create_shouldBuildProductAssignIdAndSave() {
        ProductRequest req = new ProductRequest(
                "Pampers",
                ProductType.DIAPER,
                "PACK",
                Size.M,
                ContentUnit.UNIT,
                10,
                new BigDecimal("123.45")
        );

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        // when
        Product created = service.create(req);

        // then
        assertThat(created.getProductID()).isNotBlank();

        verify(repository).save(productCaptor.capture());
        Product saved = productCaptor.getValue();

        assertThat(saved.getProductID()).isEqualTo(created.getProductID());
        assertThat(saved.getBrand()).isEqualTo("Pampers");
        assertThat(saved.getType()).isEqualTo(ProductType.DIAPER);
        assertThat(saved.getSize()).isEqualTo(Size.M);
        assertThat(saved.getPresentation()).isEqualTo("PACK");
        assertThat(saved.getContentUnit()).isEqualTo(ContentUnit.UNIT);
        assertThat(saved.getContentQty()).isEqualTo(10);
        assertThat(saved.getPrice()).isEqualByComparingTo("123.45");
    }

    @Test
    @DisplayName("create() should fail with IllegalArgumentException when type is null")
    void create_shouldFailWhenTypeIsNull() {
        ProductRequest req = new ProductRequest(
                "Pampers",
                null,
                "PACK",
                Size.M,
                ContentUnit.UNIT,
                10,
                new BigDecimal("123.45")
        );

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("type es requerido");

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("create() should fail with IllegalArgumentException when contentQty is <= 0")
    void create_shouldFailWhenContentQtyIsNotPositive() {
        ProductRequest req = new ProductRequest(
                "Pampers",
                ProductType.DIAPER,
                "PACK",
                Size.M,
                ContentUnit.UNIT,
                0,
                new BigDecimal("123.45")
        );

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("contentQty debe ser > 0");

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("create() should fail with IllegalArgumentException when brand is blank or empty")
    void create_shouldFailWhenBrandIsBlank() {
        ProductRequest req = new ProductRequest(
                "",
                ProductType.DIAPER,
                "PACK",
                Size.M,
                ContentUnit.UNIT,
                10,
                new BigDecimal("123.45")
        );

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("brand es requerido");

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("patch() should build a partial Product with only non-null fields and delegate to repository")
    void patch_shouldBuildPartialProductAndDelegateToRepository() {
        ProductPatchRequest req = new ProductPatchRequest(
                "Huggies",
                null,
                "",
                Size.G,
                ContentUnit.UNIT,
                20,
                new BigDecimal("150.00")
        );

        String id = "prod-123";

        Product updated = new Product();
        updated.setProductID(id);
        updated.setBrand("Huggies");
        updated.setSize(Size.G);
        updated.setContentUnit(ContentUnit.UNIT);
        updated.setContentQty(20);
        updated.setPrice(new BigDecimal("150.00"));

        when(repository.patch(any(Product.class))).thenReturn(updated);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        // when
        Product result = service.patch(id, req);

        // then
        assertThat(result).isSameAs(updated);

        verify(repository).patch(productCaptor.capture());
        Product partial = productCaptor.getValue();

        assertThat(partial.getProductID()).isEqualTo(id);
        assertThat(partial.getBrand()).isEqualTo("Huggies");
        assertThat(partial.getSize()).isEqualTo(Size.G);
        assertThat(partial.getContentUnit()).isEqualTo(ContentUnit.UNIT);
        assertThat(partial.getContentQty()).isEqualTo(20);
        assertThat(partial.getPrice()).isEqualByComparingTo("150.00");

        assertThat(partial.getType()).isNull();
        assertThat(partial.getPresentation()).isNull();
    }

    @Test
    @DisplayName("patch() should throw ProductNotFoundException when DynamoDB conditional check fails")
    void patch_shouldThrowProductNotFoundWhenConditionalCheckFails() {
        ProductPatchRequest req = new ProductPatchRequest(
                "Huggies",
                null,
                null,
                null,
                null,
                null,
                null
        );

        String id = "no-existe";

        when(repository.patch(any(Product.class)))
                .thenThrow(ConditionalCheckFailedException.builder().build());

        assertThatThrownBy(() -> service.patch(id, req))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining(id);
    }
}
