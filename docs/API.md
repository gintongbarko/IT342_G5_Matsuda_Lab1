# Timesheet API

## API Standards

- **Base URL (backend/web):** `http://localhost:8080`
- **Base URL (mobile emulator):** `http://10.0.2.2:8080`
- **Format:** JSON for all requests/responses
- **Authentication:** Bearer token (JWT) in `Authorization` header

> Notes for alignment:
> - Current implementation uses `/api/...` routes (no `/api/v1`).
> - Web uses `REACT_APP_API_URL` (default `http://localhost:8080`).
> - Mobile uses `BuildConfig.API_BASE_URL` (default `http://10.0.2.2:8080`).

### Response Structure

#### Standardized Target Structure
```json
{
  "success": true,
  "data": {},
  "error": null,
  "timestamp": "2024-01-28T10:30:00Z"
}
```

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "AUTH-001",
    "message": "Invalid credentials",
    "details": "Email or password is incorrect"
  },
  "timestamp": "2024-01-28T10:30:00Z"
}
```

#### Current Implementation Structure (backend/web/mobile-aligned)
- **Success responses:** direct payload (example: `{ "token": "...", "user": { ... } }`)
- **Error responses:**
```json
{
  "error": "message"
}
```

## Endpoint Specifications

## Authentication Endpoints

### POST /api/auth/register
Register a new user.

**Body (current implementation):**
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "role": "EMPLOYER|EMPLOYEE",
  "employerUsername": "string|null"
}
```

**Response (201):**
```json
{
  "token": "jwt-token",
  "user": {
    "userId": 1,
    "username": "string",
    "email": "string",
    "role": "EMPLOYER",
    "employerName": null
  }
}
```

> Not implemented in current backend contract: `confirmPassword`, `fullName`, `refreshToken`.

### POST /api/auth/login
Authenticate and get token. `username` accepts username or email.

**Body (current implementation):**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response (200):**
```json
{
  "token": "jwt-token",
  "user": {
    "userId": 1,
    "username": "string",
    "email": "string",
    "role": "EMPLOYEE",
    "employerName": "employer_username"
  }
}
```

> Not implemented in current backend contract: `refreshToken`.

### POST /api/auth/logout
Invalidate current session.

**Headers:**
`Authorization: Bearer {token}`

**Response (200):** Empty body

### GET /api/user/me (Protected)
Get current user profile.

**Headers:**
`Authorization: Bearer {token}`

**Response (200):**
```json
{
  "userId": 1,
  "username": "string",
  "email": "string",
  "role": "EMPLOYEE",
  "employerName": "employer_username"
}
```

### GET /api/auth/employers/search?q={query}
Search employers for employee registration.

**Response (200):**
```json
[
  {
    "userId": 1,
    "username": "employer1",
    "email": "employer1@example.com",
    "role": "EMPLOYER",
    "employerName": null
  }
]
```

## Error Handling

### HTTP Status Codes
- `200 OK` - Successful request
- `201 Created` - Resource created
- `400 Bad Request` - Invalid input / validation errors
- `401 Unauthorized` - Authentication required or token invalid/expired
- `403 Forbidden` - Insufficient permissions / account disabled/locked
- `404 Not Found` - Resource does not exist (not currently emitted by auth endpoints)
- `409 Conflict` - Duplicate resource (username/email already exists)
- `500 Internal Server Error` - Server error (fallback)

### Error Code Examples (standardized target format)
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "AUTH-001",
    "message": "Invalid credentials",
    "details": "Email or password is incorrect"
  },
  "timestamp": "2024-01-28T10:30:00Z"
}
```

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "VALID-001",
    "message": "Validation failed",
    "details": {
      "email": "Email is required",
      "password": "Must be at least 8 characters"
    }
  },
  "timestamp": "2024-01-28T10:30:00Z"
}
```

### Current Error Payload Example (implemented)
```json
{
  "error": "Invalid credentials"
}
```

### Common Error Codes (target catalog)
- `AUTH-001`: Invalid credentials
- `AUTH-002`: Token expired
- `AUTH-003`: Insufficient permissions
- `VALID-001`: Validation failed
- `DB-001`: Resource not found
- `DB-002`: Duplicate entry
- `BUSINESS-001`: Business rule violation
- `SYSTEM-001`: Internal server error

## Database
MySQL with tables: `users`, `user_sessions`, `employees`, `timesheet_records`. Passwords are hashed with BCrypt.
