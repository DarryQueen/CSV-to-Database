#Create department table:
CREATE TABLE department (
	dep_id int(1) NOT NULL,
	dep_name char(20) NOT NULL,
	PRIMARY KEY (dep_id)
);

#Create employee table:
CREATE TABLE employee (
	emp_id char(10) NOT NULL,
	type char(10) NOT NULL,
	first_name char(30) NOT NULL,
	last_name char(30) NOT NULL,
	gender char(10) NOT NULL,
	hire_date date NOT NULL,
	termination_date date,
	department1 int(1) NOT NULL,
	department2 int(1),
	bonus int(10),
	manager_start_date date,
	FOREIGN KEY (department1) REFERENCES department(dep_id),
	FOREIGN KEY (department2) REFERENCES department(dep_id),
	PRIMARY KEY (emp_id)
);

#Create salary table:
CREATE TABLE salary (
	emp_id char(10) NOT NULL,
	start_date date NOT NULL,
	end_date date,
	salary_amt int(10),
	FOREIGN KEY (emp_id) REFERENCES employee(emp_id)
);