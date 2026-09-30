package com.cms.service;

import com.cms.dto.ItemComponentRequest;
import com.cms.dto.ItemImportResponse;
import com.cms.dto.ItemRequest;
import com.cms.dto.ItemResponse;
import com.cms.entity.Category;
import com.cms.entity.Item;
import com.cms.entity.ItemComponent;
import com.cms.exception.BusinessException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.repository.CategoryRepository;
import com.cms.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository repo;
    private final CategoryRepository cats;
    private final ItemMapper mapper;


    @Transactional
    public ItemResponse create(ItemRequest r) {

        Category category = cats.findById(r.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (repo.findByCategoryIdAndNameIgnoreCase(category.getId(), r.name()).isPresent()) {

            throw new BusinessException("Item already exists in this category");
        }

        Item item = Item.builder().category(category).name(r.name().trim()).unitOfMeasure(r.unitOfMeasure().trim()).components(new ArrayList<>()).build();

        List<ItemComponentRequest> components = r.components() == null ? List.of() : r.components();

        if (!components.isEmpty()) {

            BigDecimal itemDefaultPrice = BigDecimal.ZERO;

            for (ItemComponentRequest componentRequest : components) {

                // component total =
                // quantity × unit price
                BigDecimal componentTotal = componentRequest.quantity().multiply(componentRequest.unitPrice());

                // Create component entity
                ItemComponent component = ItemComponent.builder().item(item).name(componentRequest.name().trim()).quantity(componentRequest.quantity()).unit(componentRequest.unit().trim()).unitPrice(componentRequest.unitPrice()).totalPrice(componentTotal).build();

                // Add component to item
                item.getComponents().add(component);

                // item price =
                // sum of all component totals
                itemDefaultPrice = itemDefaultPrice.add(componentTotal);
            }

            // Save calculated Item price
            item.setDefaultUnitPrice(itemDefaultPrice);

        }

        else {

            item.setDefaultUnitPrice(r.defaultUnitPrice());
        }

        Item savedItem = repo.save(item);

        return mapper.toResponse(savedItem);
    }


    @Transactional(readOnly = true)
    public List<ItemResponse> all() {

        return repo.findAll().stream().map(mapper::toResponse).toList();
    }


    @Transactional(readOnly = true)
    public ItemResponse getById(Long id) {

        Item item = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        return mapper.toResponse(item);
    }

    @Transactional
    public ItemResponse update(Long id, ItemRequest r) {

        Item item = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        Category category = cats.findById(r.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Optional<Item> duplicate = repo.findByCategoryIdAndNameIgnoreCase(category.getId(), r.name());

        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {

            throw new BusinessException("Item already exists in this category");
        }

        // 4. Update basic fields
        item.setCategory(category);

        item.setName(r.name().trim());

        item.setUnitOfMeasure(r.unitOfMeasure().trim());


        item.getComponents().clear();


        List<ItemComponentRequest> components = r.components() == null ? List.of() : r.components();

        if (!components.isEmpty()) {

            BigDecimal itemDefaultPrice = BigDecimal.ZERO;

            for (ItemComponentRequest componentRequest : components) {

                // quantity × unit price
                BigDecimal componentTotal = componentRequest.quantity().multiply(componentRequest.unitPrice());

                ItemComponent component = ItemComponent.builder().item(item).name(componentRequest.name().trim()).quantity(componentRequest.quantity()).unit(componentRequest.unit().trim()).unitPrice(componentRequest.unitPrice()).totalPrice(componentTotal).build();

                item.getComponents().add(component);

                itemDefaultPrice = itemDefaultPrice.add(componentTotal);
            }

            // Recalculate Item price
            item.setDefaultUnitPrice(itemDefaultPrice);

        }


        else {

            item.setDefaultUnitPrice(r.defaultUnitPrice());
        }

        // 5. Save updated item
        Item savedItem = repo.save(item);

        // 6. Return response
        return mapper.toResponse(savedItem);
    }

    // =========================================================
    // EXCEL IMPORT
    // =========================================================

    @Transactional
    public ItemImportResponse importExcel(MultipartFile file) {

        // 1. Validate file
        if (file.isEmpty() || !Objects.requireNonNullElse(file.getOriginalFilename(), "").toLowerCase().endsWith(".xlsx")) {

            throw new BusinessException("Only non-empty .xlsx files are supported");
        }

        List<String> errors = new ArrayList<>();

        int count = 0;

        try (InputStream in = file.getInputStream();

             Workbook wb = WorkbookFactory.create(in)) {

            Sheet sheet = wb.getSheetAt(0);

            int rowNo = 0;

            for (Row row : sheet) {

                rowNo++;

                // Skip header
                if (rowNo == 1) {
                    continue;
                }

                try {

                    // Current Excel format:
                    //
                    // Column 0 = category
                    // Column 1 = item name
                    // Column 2 = price
                    // Column 3 = unit

                    String categoryName = get(row, 0);

                    String itemName = get(row, 1);

                    String price = get(row, 2);

                    String unit = get(row, 3);

                    // Validate required values
                    if (categoryName.isBlank() || itemName.isBlank() || price.isBlank() || unit.isBlank()) {

                        throw new IllegalArgumentException("category, name, price and unit are required");
                    }

                    // Convert price
                    BigDecimal itemPrice = new BigDecimal(price);

                    if (itemPrice.signum() < 0) {

                        throw new IllegalArgumentException("price must be non-negative");
                    }

                    // Find category or create it
                    Category category = cats.findByNameIgnoreCase(categoryName).orElseGet(() -> cats.save(Category.builder().name(categoryName).build()));

                    // Check duplicate
                    if (repo.findByCategoryIdAndNameIgnoreCase(category.getId(), itemName).isPresent()) {

                        throw new IllegalArgumentException("duplicate item");
                    }

                    // Create normal item
                    Item item = Item.builder().category(category).name(itemName).defaultUnitPrice(itemPrice).unitOfMeasure(unit).components(new ArrayList<>()).build();

                    repo.save(item);

                    count++;

                } catch (Exception ex) {

                    errors.add("Row " + rowNo + ": " + ex.getMessage());
                }
            }

        } catch (Exception ex) {

            throw new BusinessException("Could not read Excel file: " + ex.getMessage());
        }

        return new ItemImportResponse(count, errors);
    }

    // =========================================================
    // EXCEL CELL READER
    // =========================================================

    private String get(Row row, int index) {

        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

        if (cell == null) {
            return "";
        }

        if (cell.getCellType() == CellType.NUMERIC) {

            return BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
        }

        return cell.toString().trim();
    }
}