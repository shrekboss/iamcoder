package org.coder.err.programming._2_design_chapter.redundantcode.templatemethod.right;

import org.coder.err.programming._2_design_chapter.redundantcode.templatemethod.Item;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service(value = "NormalUserCart")
public class NormalUserCart extends AbstractCart {

    @Override
    protected void processCouponPrice(long userId, Item item) {
        item.setCouponPrice(BigDecimal.ZERO);
    }

    @Override
    protected void processDeliveryPrice(long userId, Item item) {
        item.setDeliveryPrice(item.getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()))
                .multiply(new BigDecimal("0.1")));
    }
}
