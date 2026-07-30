package com.mall.user.controller;

import com.mall.common.context.UserContext;
import com.mall.common.result.Result;
import com.mall.user.dto.AddressDTO;
import com.mall.user.entity.UserAddress;
import com.mall.user.service.AddressService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public Result<List<UserAddress>> list() {
        return Result.success(addressService.listByUser(UserContext.getUserId()));
    }

    @PostMapping
    public Result<UserAddress> add(@Valid @RequestBody AddressDTO dto) {
        return Result.success(addressService.add(UserContext.getUserId(), dto));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AddressDTO dto) {
        addressService.update(UserContext.getUserId(), id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        addressService.delete(UserContext.getUserId(), id);
        return Result.success();
    }

    @PutMapping("/{id}/default")
    public Result<Void> setDefault(@PathVariable Long id) {
        addressService.setDefault(UserContext.getUserId(), id);
        return Result.success();
    }
}
