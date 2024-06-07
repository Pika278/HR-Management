# HR-Management

### Công nghệ sử dụng: 
- Front end: Thymeleaf
- Back end: Java Spring Boot, Spring Security, Spring Jpa, Hibernate
- Database: MySQL
### Chức năng chính 
- Tính năng chung (User, Admin):
  + Đăng nhập
  + Checkin/Checkout
  + Xem danh sách nhân viên, danh sách phòng ban
  + Xem danh sách thông báo
  + Quên mật khẩu (gửi mail verìfy)
  + Đổi mật khẩu
  
- Admin:
  + Tạo tài khoản mới(gửi mail verìfy tài khoản tạo thành công)
  + Xem/sửa/xóa/khóa tài khoản nhân viên
  + Xem/sửa/xóa phòng ban
  + Lập lịch gửi thông báo mới (gửi bằng server sent event)
  + Chỉnh sửa/xóa thông báo (nếu thông báo chưa được gửi)
  + Thêm/sửa/xóa chấm công
