package com.kma.project.expensemanagement.controller;

import com.kma.project.expensemanagement.dto.authen.SignUpRequest;
import com.kma.project.expensemanagement.dto.request.UserInputDto;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.dto.response.UserOutputDto;
import com.kma.project.expensemanagement.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/users")
@Api(tags = "Quản lí người dùng")
public class UserController {
    @Autowired
    UserService userService;

    @ApiOperation(value = "Thêm mới tài khoản")
    @PostMapping("/sign-up")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.ok(userService.signUp(request));
    }

    @ApiOperation(value = "Lấy danh sách tài khoản")
    @GetMapping
    public PageResponse<UserOutputDto> getAllUser(Integer page, Integer size, String sort, String search) {
        return userService.getAllUser(page, size, sort, search);
    }

    @ApiOperation(value = "Cập nhật tài khoản")
    @PutMapping("{id}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserInputDto request, @PathVariable("id") Long userId) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @ApiOperation(value = "Xóa tài khoản")
    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable("id") Long userId) {
        userService.delete(userId);
    }

    @ApiOperation(value = "Lấy chi tiết tài khoản")
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getDetailUser(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(userService.getDetailUser(userId));
    }


}
