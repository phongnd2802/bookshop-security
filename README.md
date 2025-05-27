# BookShopWeb
Dự án Java Web xây dựng một Shop Bán Sách

## A. Set up project BookShopWeb

### 1. Nạp hình
Tạo thư mục C:/var/webapp/images và giải nén tất cả hình vào thư mục này.

### 2. Tạo database
Cài đặt MySQL Workbench
MYSQL chạy ở port 3306
Tạo database: bookshopdb
Chạy file bookshopdb.sql

### 3. Cấu hình Tomcat
* Cài đặt Tomcat 9
* Mở IDEA IntelliJ và thêm vào File | Settings | Build, Execution, Deployment | Application Servers > Tomcat Server (Tomcat Home trỏ đến thư mục Tomcat, ví dụ: apache-tomcat-9.0.xxx)
* Current File -> Edit Configuration... -> Add new -> Tomcat Server Local
* [Fix] -> BookShopWeb:war exploded -> OK

### 4. Run

## B. Cấu hình utils.ConstantUtils
* Mặc định, DB_NAME là bookshopdb, DB_USERNAME là root, DB_PASSWORD là 12345.
* Có thể thay đổi nếu như khác.
