package com.kodekart.app;

import java.util.List;
import java.util.Scanner;

import com.kodekart.dao.UserDAO;
import com.kodekart.model.Product;
import com.kodekart.model.User;
import com.kodekart.service.KodeKartService;

public class Main {
	private static Scanner sc = new Scanner(System.in);
	private static UserDAO userDAO = new UserDAO();
	private static KodeKartService service = new KodeKartService();

	public static void main(String[] args) {
		System.out.println("=== Welcome to KodeKart (Console) ===");
		while (true) {
			System.out.println("\nMain Menu:");
			System.out.println("1. Register");
			System.out.println("2. Login");
			System.out.println("3. Exit");
			System.out.print("Choice: ");
			String c = sc.nextLine();
			if ("1".equals(c))
				register();
			else if ("2".equals(c))
				login();
			else if ("3".equals(c)) {
				System.out.println("Goodbye!");
				break;
			} else
				System.out.println("Invalid choice.");
		}
		sc.close();
	}

	private static void register() {
		System.out.println("--- Register new user ---");
		System.out.print("Name: ");
		String name = sc.nextLine();
		System.out.print("Email: ");
		String email = sc.nextLine();
		System.out.print("Phone: ");
		String phone = sc.nextLine();
		System.out.print("Password: ");
		String pwd = sc.nextLine();

		User u = new User();
		u.setName(name);
		u.setEmail(email);
		u.setPhone(phone);
		u.setPassword(pwd);
		u.setAdmin(false);
		boolean ok = userDAO.register(u);
		System.out.println(ok ? "Registration successful." : "Registration failed.");
	}

	private static void login() {
		System.out.println("--- Login ---");
		System.out.print("Email: ");
		String email = sc.nextLine().trim();
		System.out.print("Password: ");
		String pwd = sc.nextLine().trim();
		User u = userDAO.login(email, pwd);
		if (u == null) {
			System.out.println("Invalid credentials.");
			return;
		}
		System.out.println("Welcome, " + u.getName());
		if (u.isAdmin())
			adminMenu(u);
		else
			userMenu(u);
	}

	private static void userMenu(User u) {
		while (true) {
			System.out.println("\nUser Menu:");
			System.out.println("1. View Products");
			System.out.println("2. Search Products");
			System.out.println("3. Add to Cart");
			System.out.println("4. View Cart");
			System.out.println("5. Place Order");
			System.out.println("6. My Orders");
			System.out.println("7. Logout");
			System.out.print("Choice: ");
			String ch = sc.nextLine().trim();
			try {
				switch (ch) {
				case "1":
					listProducts();
					break;
				case "2":
					System.out.print("Keyword: ");
					String q = sc.nextLine().trim();
					service.searchProducts(q).forEach(System.out::println);
					break;
				case "3":
					System.out.print("Product ID: ");
					int pid = Integer.parseInt(sc.nextLine().trim());
					System.out.print("Quantity: ");
					int qty = Integer.parseInt(sc.nextLine().trim());
					boolean added = service.addToCart(u.getId(), pid, qty);
					System.out.println(added ? "Added to cart." : "Failed to add.");
					break;
				case "4":
					service.viewCart(u.getId()).forEach(System.out::println);
					break;
				case "5":
					service.placeOrder(u.getId());
					break;
				case "6":
					// show orders for user
					com.kodekart.dao.OrderDAO orderDAO = new com.kodekart.dao.OrderDAO();
					orderDAO.getOrdersByUser(u.getId()).forEach(System.out::println);
					break;
				case "7":
					System.out.println("Logged out.");
					return;
				default:
					System.out.println("Invalid choice.");
				}
			} catch (NumberFormatException ex) {
				System.out.println("Invalid number input.");
			}
		}
	}

	private static void adminMenu(User u) {
		while (true) {
			System.out.println("\nAdmin Menu:");
			System.out.println("1. Add Product");
			System.out.println("2. Update Product");
			System.out.println("3. Delete Product");
			System.out.println("4. View All Products");
			System.out.println("5. View All Orders");
			System.out.println("6. Logout");
			System.out.print("Choice: ");
			String ch = sc.nextLine().trim();
			try {
				switch (ch) {
				case "1":
					Product p = new Product();
					System.out.print("Name: ");
					p.setName(sc.nextLine().trim());
					System.out.print("Category: ");
					p.setCategory(sc.nextLine().trim());
					System.out.print("Price: ");
					p.setPrice(Double.parseDouble(sc.nextLine().trim()));
					System.out.print("Quantity: ");
					p.setQuantity(Integer.parseInt(sc.nextLine().trim()));
					System.out.print("Description: ");
					p.setDescription(sc.nextLine().trim());
					System.out.println(service.addProduct(p) ? "Product added." : "Add failed.");
					break;
				case "2":
					System.out.print("Product ID to update: ");
					int id = Integer.parseInt(sc.nextLine().trim());
					Product ep = service.getProductById(id);
					if (ep == null) {
						System.out.println("Not found.");
						break;
					}
					System.out.print("New name (" + ep.getName() + "): ");
					String nn = sc.nextLine().trim();
					if (!nn.isEmpty())
						ep.setName(nn);
					System.out.print("New category (" + ep.getCategory() + "): ");
					String nc = sc.nextLine().trim();
					if (!nc.isEmpty())
						ep.setCategory(nc);
					System.out.print("New price (" + ep.getPrice() + "): ");
					String np = sc.nextLine().trim();
					if (!np.isEmpty())
						ep.setPrice(Double.parseDouble(np));
					System.out.print("New qty (" + ep.getQuantity() + "): ");
					String nq = sc.nextLine().trim();
					if (!nq.isEmpty())
						ep.setQuantity(Integer.parseInt(nq));
					System.out.print("New desc (leave empty to keep): ");
					String nd = sc.nextLine().trim();
					if (!nd.isEmpty())
						ep.setDescription(nd);
					System.out.println(service.updateProduct(ep) ? "Updated." : "Update failed.");
					break;
				case "3":
					System.out.print("Product ID to delete: ");
					int did = Integer.parseInt(sc.nextLine().trim());
					System.out.println(service.deleteProduct(did) ? "Deleted." : "Delete failed.");
					break;
				case "4":
					listProducts();
					break;
				case "5":
					com.kodekart.dao.OrderDAO orderDAO = new com.kodekart.dao.OrderDAO();
					orderDAO.getAllOrders().forEach(System.out::println);
					break;
				case "6":
					System.out.println("Logged out (admin).");
					return;
				default:
					System.out.println("Invalid choice.");
				}
			} catch (NumberFormatException ex) {
				System.out.println("Invalid number input.");
			}
		}
	}

	private static void listProducts() {
		List<Product> list = service.listAllProducts();
		if (list.isEmpty())
			System.out.println("No products available.");
		else
			list.forEach(System.out::println);
	}
}
