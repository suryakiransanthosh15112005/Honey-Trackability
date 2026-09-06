package com.honeychain.marketplace.dto;

import java.math.BigDecimal;

public class ProductFilterRequest {

    private String search;
    private String region;
    private String flowerSource;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minPurity;
    private BigDecimal minRating;
    private String sortBy = "newest"; // priceAsc, priceDesc, ratingDesc, purityDesc, newest
    private int page = 0;
    private int size = 12;

    public ProductFilterRequest() {
    }

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getFlowerSource() { return flowerSource; }
    public void setFlowerSource(String flowerSource) { this.flowerSource = flowerSource; }
    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
    public Integer getMinPurity() { return minPurity; }
    public void setMinPurity(Integer minPurity) { this.minPurity = minPurity; }
    public BigDecimal getMinRating() { return minRating; }
    public void setMinRating(BigDecimal minRating) { this.minRating = minRating; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
