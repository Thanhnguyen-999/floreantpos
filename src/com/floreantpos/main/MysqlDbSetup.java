package com.floreantpos.main;

import com.floreantpos.Database;
import com.floreantpos.config.AppConfig;
import com.floreantpos.util.DatabaseUtil;

/**
 * Standalone helper to (re)create the FloreantPOS schema and seed data
 * into the MySQL/MariaDB database configured in floreantpos.config.properties.
 *
 * It reuses the exact same logic the app's "Create Database" button uses,
 * so it produces the default users (admin 123/1111, manager 124/2222, etc.),
 * user types, order types, currencies and sample menu items.
 *
 * WARNING: uses hbm2ddl=create, so it DROPS and recreates all tables in the
 * target database. Only run against an empty / disposable posdb.
 *
 * Run with:
 *   java -cp "<classpath>" com.floreantpos.main.MysqlDbSetup
 */
public class MysqlDbSetup {

	public static void main(String[] args) {
		Database db = Database.MYSQL;

		String host = AppConfig.getDatabaseHost();
		String port = AppConfig.getDatabasePort();
		String name = AppConfig.getDatabaseName();
		String user = AppConfig.getDatabaseUser();
		String pass = AppConfig.getDatabasePassword();
		if (pass == null) {
			pass = "";
		}

		// Server is configured with utf8mb4 + Barracuda + innodb_large_prefix=ON,
		// so the *_PROPERTIES composite PK (id, property_name varchar(255)) fits
		// within the enlarged index-key limit. Keep the UTF-8 connection so all
		// tables are created as Unicode and can store Vietnamese text.
		String createConnectString = db.getCreateDbConnectString(host, port, name);

		System.out.println("=== FloreantPOS MySQL DB setup ===");
		System.out.println("Provider : " + db.getProviderName());
		System.out.println("Driver   : " + db.getHibernateConnectionDriverClass());
		System.out.println("Dialect  : " + db.getHibernateDialect());
		System.out.println("URL      : " + createConnectString);
		System.out.println("User     : " + user);
		System.out.println("Seeding schema + sample data (hbm2ddl=create)...");

		boolean ok = DatabaseUtil.createDatabase(
				createConnectString,
				db.getHibernateDialect(),
				db.getHibernateConnectionDriverClass(),
				user,
				pass,
				true /* exportSampleData */);

		if (ok) {
			System.out.println("RESULT: SUCCESS - database created and seeded.");
			System.out.println("Default admin login -> user 123 / password 1111");
		} else {
			System.out.println("RESULT: FAILED - see log output above.");
		}

		// c3p0 / derby background threads can keep the JVM alive; force exit.
		System.exit(ok ? 0 : 1);
	}
}
