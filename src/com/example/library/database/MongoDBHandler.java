package com.example.library.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import com.example.library.util.PasswordUtil;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class MongoDBHandler {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> emailCollection;
    private MongoCollection<Document> userCollection;

    public MongoDBHandler() {
        String mongoUri = "mongodb+srv://admin:c2fs8vzN5QNNYK9T@express-test.oqp8nev.mongodb.net/?retryWrites=true&w=majority&appName=express-test";
        MongoClient mongoClient = MongoClients.create(mongoUri);
        MongoDatabase database = mongoClient.getDatabase("networkfinal");
        emailCollection = database.getCollection("emails");
        userCollection = database.getCollection("users");
    }

    // Đóng kết nối MongoDB
    public void close() {
        mongoClient.close();
    }

    // Lưu email vào MongoDB
    public void saveEmail(String from, String to, String subject, String content) {
        Document email = new Document("from", from)
                .append("to", to)
                .append("subject", subject)
                .append("content", content)
                .append("timestamp", System.currentTimeMillis());

        emailCollection.insertOne(email);
    }

    public boolean authenticateUser(String username, String password) {
        Document user = userCollection.find(Filters.eq("username", username)).first();
        if (user != null) {
            String hashedPassword = user.getString("password");
            return PasswordUtil.checkPassword(password, hashedPassword); // Kiểm tra mật khẩu đã băm
        }
        return false;
    }

    // Hàm thêm người dùng
    public boolean addUser(String username, String password) {
        Document existingUser = userCollection.find(Filters.eq("username", username)).first();
        if (existingUser != null) {
            return false; // Tài khoản đã tồn tại
        }
        String hashedPassword = PasswordUtil.hashPassword(password); // Băm mật khẩu
        Document newUser = new Document("username", username)
                .append("password", hashedPassword);
        userCollection.insertOne(newUser);
        return true;
    }
    // Lấy danh sách email của người dùng
    public List<Document> getUserEmails(String username) {
        FindIterable<Document> iterable = emailCollection.find(Filters.eq("to", username));
        List<Document> emails = new ArrayList<>();
        for (Document doc : iterable) {
            emails.add(doc);
        }
        return emails;
    }

    // Cập nhật email (ví dụ: đánh dấu đã đọc)
    public void updateEmail(ObjectId emailId, String key, Object value) {
        emailCollection.updateOne(Filters.eq("_id", emailId), Updates.set(key, value));
    }

    // Xóa email
    public void deleteEmail(ObjectId emailId) {
        emailCollection.deleteOne(Filters.eq("_id", emailId));
    }
}
