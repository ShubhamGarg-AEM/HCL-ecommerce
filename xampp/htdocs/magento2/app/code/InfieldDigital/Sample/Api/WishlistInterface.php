<?php


namespace InfieldDigital\Sample\Api;

use Magento\Wishlist\Model\Item;

interface WishlistInterface
{

    /**
     * Get the amount of items in the wishlist
     *
     * @return int
     */
    public function getItemsCount();

    /**
     * Get the wishlist items
     *
     * @return \InfieldDigital\Sample\Api\ItemInterface[]
     */
    public function getItems();

    /**
     * Retrieve wishlist item collection
     *
     * @param int $itemId
     * @return false|Item
     */
    public function getItem($itemId);

    /**
     * Adds new product to wishlist.
     * Returns new item or string on error.
     *
     * @param int|\Magento\Catalog\Model\Product $product
     * @param \Magento\Framework\DataObject|array|string|null $buyRequest
     * @param bool $forciblySetQty
     * @throws \Magento\Framework\Exception\LocalizedException
     * @return Item|string
     */
    public function addNewItem($product, $buyRequest = null, $forciblySetQty = false);
}