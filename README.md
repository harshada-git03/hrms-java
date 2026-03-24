# NexHR — Backend API

A production-grade HRMS REST API built with Spring Boot 3.5 + Java 21.

## Tech Stack
- **Framework:** Spring Boot 3.5, Java 21
- **Database:** PostgreSQL (Supabase)
- **Auth:** JWT (JJWT 0.12.5) + Spring Security
- **AI:** Google Gemini 2.5 Flash
- **Deploy:** Railway

## Modules
| Module | Endpoints |
|--------|-----------|
| Auth | Register, Login, JWT |
| Employees | CRUD, RBAC |
| Attendance | Clock in/out, Logs |
| Leave | Apply, Approve, Reject |
| Payroll | Generate, Payslips |
| Holidays | Indian Calendar API |
| Announcements | Create, Target by role |
| Documents | E-sign workflow |
| Dashboard | Live team stats |
| AI Chatbot | Gemini-powered HR assistant |

## Setup
```bash
# Clone
git clone https://github.com/harshada-git03/hrms-java.git
cd hrms-java

# Create application-local.yml
cp src/main/resources/application.yml src/main/resources/application-local.yml
# Fill in your DB, JWT, Gemini credentials

# Run
./mvnw spring-boot:run "-Dspring-boot.run.profiles=local"