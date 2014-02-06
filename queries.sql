#Select names and departments of all managers:
SELECT first_name,last_name,department1,department2 FROM employee WHERE type="MANAGER";

#Select all male employees:
SELECT * FROM employee WHERE gender="MALE";

#Select ID of departments with no manager currently hired:
SELECT dep_id FROM department
WHERE dep_id NOT IN (SELECT department1 FROM employee WHERE type="MANAGER" and termination_date IS NULL)
OR dep_id NOT IN (SELECT department2 FROM employee WHERE type="MANAGER" and termination_date IS NULL);

#Select salary, first name, last name, and gender of employees currently hired with lowest salary:
SELECT salary_amt,first_name,last_name,gender
FROM salary LEFT JOIN employee
	ON salary.emp_id=employee.emp_id
WHERE salary_amt=(SELECT MIN(salary_amt) FROM salary WHERE end_date IS NULL) AND (end_date IS NULL);

#Select name and salary of highest paid employee in "ENGINEERING" who is not a manager:
SELECT first_name,last_name,salary_amt,department1,department2
FROM salary RIGHT JOIN employee
	ON salary.emp_id=employee.emp_id
WHERE salary_amt=(SELECT MAX(salary_amt) FROM salary WHERE 
	salary.emp_id IN (SELECT employee.emp_id FROM employee,department WHERE
		employee.type<>"MANAGER"
		AND (employee.department1=department.dep_id OR employee.department2=department.dep_id)
		AND department.dep_name="ENGINEERING"))
	AND employee.type<>"MANAGER"
	AND (employee.department1 IN (SELECT dep_id FROM department WHERE dep_name="ENGINEERING")
		OR employee.department2 IN (SELECT dep_id FROM department WHERE dep_name="ENGINEERING"));