# Foorend API 명세서

## 목차
1. [공통 응답 구조](#공통-응답-구조)
2. [인증](#인증)
3. [공통 코드 API](#공통-코드-api)
4. [사용자 API](#사용자-api)
5. [모임 API](#모임-api)
6. [식당 API](#식당-api)

---

## 공통 응답 구조

### BaseRes
모든 API 응답의 기본 구조입니다.

```json
{
  "code": 0,
  "message": "Success"
}
```

- `code`: 리턴 코드 (0: 정상, 그 외: 비정상)
- `message`: 리턴 메시지

### BaseGenericRes<T>
데이터를 포함하는 단일 객체 응답입니다.

```json
{
  "code": 0,
  "message": "Success",
  "data": { ... }
}
```

### BaseGenericListRes<T>
리스트 데이터를 포함하는 응답입니다.

```json
{
  "code": 0,
  "message": "Success",
  "data": [ ... ],
  "totalCount": 10
}
```

---

## 인증

### OAuth2 로그인
- **엔드포인트**: `/login/oauth2/code/google`
- **메서드**: `GET`
- **설명**: Google OAuth2를 통한 로그인
- **인증**: 불필요
- **응답**: JWT 토큰 발급 후 리다이렉트

---

## 공통 코드 API

### 1. 국적 목록 조회
- **엔드포인트**: `GET /api/common/nationalities`
- **설명**: 전세계 국가 목록을 조회합니다. (국가코드 포함)
- **인증**: 불필요

**응답**
```json
{
  "code": 0,
  "message": "Success",
  "data": [
    {
      "code": "KR",
      "name": "대한민국",
      "dialCode": "+82"
    }
  ],
  "totalCount": 195
}
```

### 2. 성별 목록 조회
- **엔드포인트**: `GET /api/common/genders`
- **설명**: 성별 목록을 조회합니다.
- **인증**: 불필요

**응답**
```json
{
  "code": 0,
  "message": "Success",
  "data": [
    {
      "code": "MALE",
      "name": "남성"
    },
    {
      "code": "FEMALE",
      "name": "여성"
    }
  ],
  "totalCount": 2
}
```

### 3. 연애 상태 목록 조회
- **엔드포인트**: `GET /api/common/relationship-statuses`
- **설명**: 연애 상태 목록을 조회합니다.
- **인증**: 불필요

**응답**
```json
{
  "code": 0,
  "message": "Success",
  "data": [
    {
      "code": "SINGLE",
      "name": "싱글"
    },
    {
      "code": "IN_RELATIONSHIP",
      "name": "연애 중"
    }
  ],
  "totalCount": 2
}
```

### 4. 선호 가격대 목록 조회
- **엔드포인트**: `GET /api/common/price-tiers`
- **설명**: 선호 가격대 목록을 조회합니다.
- **인증**: 불필요

**응답**
```json
{
  "code": 0,
  "message": "Success",
  "data": [
    {
      "code": "LOW",
      "name": "저렴"
    },
    {
      "code": "MID",
      "name": "보통"
    },
    {
      "code": "HIGH",
      "name": "비쌈"
    }
  ],
  "totalCount": 3
}
```

### 5. 직업 카테고리 목록 조회
- **엔드포인트**: `GET /api/common/job-categories`
- **설명**: 직업 카테고리 목록을 조회합니다.
- **인증**: 불필요

**응답**
```json
{
  "code": 0,
  "message": "Success",
  "data": [
    {
      "code": "TECH",
      "name": "IT/기술"
    },
    {
      "code": "FINANCE",
      "name": "금융"
    }
  ],
  "totalCount": 10
}
```

---

## 사용자 API

### 1. 내 정보 조회 (간단)
- **엔드포인트**: `GET /api/user`
- **설명**: 현재 로그인한 사용자의 간단 정보를 조회합니다.
- **인증**: 필요 (JWT)

**응답**
```json
{
  "code": 0,
  "message": "Success",
  "data": {
    "userId": 1,
    "email": "user@example.com",
    "name": "홍길동"
  }
}
```

### 2. 내 프로필 상세 조회
- **엔드포인트**: `GET /api/user/profile`
- **설명**: 현재 로그인한 사용자의 상세 프로필 정보를 조회합니다.
- **인증**: 필요 (JWT)

**응답**
```json
{
  "code": 0,
  "message": "Success",
  "data": {
    "userId": 1,
    "email": "user@example.com",
    "name": "홍길동",
    "phoneNumber": "01012345678",
    "gender": "MALE",
    "birthday": "1995-05-15",
    "profileImageUrl": "https://example.com/image.jpg",
    "relationshipStatus": "SINGLE",
    "nationality": "KR",
    "jobCategory": "TECH",
    "traitsAnswers": {
      "q1": "A",
      "q2": "B"
    }
  }
}
```

### 3. 회원 기본정보 입력
- **엔드포인트**: `POST /api/user/basic-info`
- **설명**: 회원가입 후 기본정보와 성향 데이터를 저장합니다.
- **인증**: 필요 (JWT)

**요청**
```json
{
  "name": "홍길동",
  "phoneNumber": "01012345678",
  "gender": "MALE",
  "birthday": "1995-05-15",
  "relationshipStatus": "SINGLE",
  "nationality": "KR",
  "jobCategory": "TECH",
  "traitsAnswers": {
    "q1": "A",
    "q2": "B"
  }
}
```

**필수 필드**
- `name`: 이름
- `gender`: 성별
- `birthday`: 생년월일

**응답**
```json
{
  "code": 0,
  "message": "Success",
  "data": true
}
```

### 4. 회원 프로필 수정
- **엔드포인트**: `PATCH /api/user/profile`
- **설명**: 회원 프로필을 부분 수정합니다. 보낸 값만 업데이트됩니다.
- **인증**: 필요 (JWT)

**요청**
```json
{
  "name": "홍길동",
  "phoneNumber": "01012345678",
  "relationshipStatus": "SINGLE",
  "jobCategory": "TECH",
  "nationality": "KR"
}
```

**모든 필드 선택적 (optional)**

**응답**
```json
{
  "code": 0,
  "message": "Success",
  "data": true
}
```

### 5. 회원 탈퇴
- **엔드포인트**: `DELETE /api/user`
- **설명**: 회원 탈퇴를 처리합니다. confirmText에 'DELETE'를 입력해야 합니다.
- **인증**: 필요 (JWT)

**요청**
```json
{
  "confirmText": "DELETE",
  "reason": "서비스를 더 이상 사용하지 않음"
}
```

**필수 필드**
- `confirmText`: "DELETE" 입력 필요

**응답**
```json
{
  "code": 0,
  "message": "Success",
  "data": true
}
```

---

## 사용자 인증 API

### 1. 로그아웃
- **엔드포인트**: `POST /api/user/logout`
- **설명**: 현재 로그인한 사용자를 로그아웃합니다. Refresh Token이 무효화됩니다.
- **인증**: 필요 (JWT)

**응답**
```json
{
  "code": 0,
  "message": "Success"
}
```

### 2. 토큰 갱신
- **엔드포인트**: `POST /api/user/refresh`
- **설명**: Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급합니다.
- **인증**: 불필요 (Refresh Token 필요)

**요청**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**필수 필드**
- `refreshToken`: Refresh Token

**응답**
```json
{
  "code": 0,
  "message": "Success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

---

## 사용자 선호 설정 API

### 1. 내 선호 설정 조회
- **엔드포인트**: `GET /api/user/preference`
- **설명**: 현재 로그인한 사용자의 선호 가격대/언어를 조회합니다. hasData가 false면 등록 필요.
- **인증**: 필요 (JWT)

**응답**
```json
{
  "code": 0,
  "message": "Success",
  "data": {
    "priceTiers": ["LOW", "MID"],
    "languages": ["KO", "EN"],
    "hasData": true
  }
}
```

### 2. 내 선호 설정 저장
- **엔드포인트**: `POST /api/user/preference`
- **설명**: 선호 가격대/언어를 저장합니다. 기존 데이터가 있으면 덮어씁니다.
- **인증**: 필요 (JWT)

**요청**
```json
{
  "priceTiers": ["LOW", "MID"],
  "languages": ["KO", "EN"]
}
```

**응답**
```json
{
  "code": 0,
  "message": "Success",
  "data": true
}
```

---

## 모임 API

### 1. 예정된 모임 조회
- **엔드포인트**: `GET /api/meetings`
- **설명**: 현재 시간 이후, 정원 미달인 모임 목록을 조회합니다.
- **인증**: 불필요

**응답**
```json
{
  "code": 0,
  "message": "Success",
  "data": [
    {
      "slotId": 1,
      "locationArea": "강남",
      "meetDate": "2025-01-20",
      "dayOfWeek": "월",
      "meetTime": "19:00:00"
    }
  ],
  "totalCount": 10
}
```

---

## 모임 참여 API

### 1. 내 모임 조회
- **엔드포인트**: `GET /api/meeting/entry/my`
- **설명**: 현재 참여 중인 모임을 조회합니다. 메인 화면에서 사용합니다.
- **인증**: 필요 (JWT)

**응답**
```json
{
  "code": 0,
  "message": "Success",
  "data": {
    "entryId": 1,
    "slotId": 1,
    "locationArea": "강남",
    "meetDate": "2025-01-20",
    "dayOfWeek": "월",
    "meetTime": "19:00:00",
    "restaurantName": "맛있는 식당",
    "restaurantAddr": "서울시 강남구 테헤란로 123",
    "members": [
      {
        "jobCategory": "TECH",
        "nationality": "KR"
      }
    ],
    "lateCount": 0,
    "isInfoRevealed": true,
    "hasActiveMeeting": true
  }
}
```

**참고**
- `isInfoRevealed`: 모임 하루 전부터 true (식당 정보, 참석자 정보 공개)
- `hasActiveMeeting`: 참여 중인 모임 여부

### 2. 모임 가입
- **엔드포인트**: `POST /api/meeting/entry`
- **설명**: 현재 로그인한 사용자가 모임에 가입합니다.
- **인증**: 필요 (JWT)

**요청**
```json
{
  "slotId": 1
}
```

**필수 필드**
- `slotId`: 모임 ID

**응답**
```json
{
  "code": 0,
  "message": "Success",
  "data": {
    "entryId": 1,
    "slotId": 1,
    "userId": 1,
    "status": "JOINED",
    "cancelReason": null,
    "createdAt": "2025-01-15T10:00:00",
    "updatedAt": "2025-01-15T10:00:00"
  }
}
```

### 3. 모임 참여 취소
- **엔드포인트**: `DELETE /api/meeting/entry`
- **설명**: 현재 참여 중인 모임을 취소합니다. 모임 시작 전(OPEN)에만 가능합니다.
- **인증**: 필요 (JWT)

**요청**
```json
{
  "cancelReason": "개인 사정으로 참여가 어렵습니다."
}
```

**필수 필드**
- `cancelReason`: 취소 사유

**응답**
```json
{
  "code": 0,
  "message": "Success",
  "data": true
}
```

### 4. 늦어요 알림
- **엔드포인트**: `POST /api/meeting/entry/late`
- **설명**: 현재 참여 중인 모임에 늦어요 상태를 알립니다.
- **인증**: 필요 (JWT)

**응답**
```json
{
  "code": 0,
  "message": "Success",
  "data": true
}
```

---

## 식당 API

### 1. 전체 식당 조회
- **엔드포인트**: `GET /api/restaurants`
- **설명**: 등록된 모든 식당을 조회합니다.
- **인증**: 불필요

**응답**
```json
{
  "code": 0,
  "message": "Success",
  "data": [
    {
      "restaurantId": 1,
      "restaurantName": "맛있는 식당",
      "restaurantAddr": "서울시 강남구 테헤란로 123",
      "locationArea": "강남",
      "avgPriceTier": "MID",
      "category": "한식"
    }
  ],
  "totalCount": 50
}
```

---

## 에러 코드

모든 API는 공통 응답 구조를 따릅니다. 에러 발생 시 `code` 필드가 0이 아닌 값으로 반환됩니다.

**예시**
```json
{
  "code": 400,
  "message": "잘못된 요청입니다."
}
```

---

## 인증 방식

### JWT 토큰 사용
대부분의 API는 JWT 토큰 인증이 필요합니다. 요청 헤더에 다음과 같이 포함해야 합니다:

```
Authorization: Bearer {accessToken}
```

### 토큰 만료
- Access Token 만료 시: `/api/user/refresh` 엔드포인트를 통해 Refresh Token으로 갱신
- Refresh Token 만료 시: 재로그인 필요

---

## 참고사항

1. 모든 날짜/시간 형식은 ISO 8601 형식을 따릅니다.
   - 날짜: `YYYY-MM-DD` (예: `2025-01-20`)
   - 시간: `HH:mm:ss` (예: `19:00:00`)
   - 날짜시간: `YYYY-MM-DDTHH:mm:ss` (예: `2025-01-15T10:00:00`)

2. 모든 문자열 필드는 UTF-8 인코딩을 사용합니다.

3. PATCH 요청은 부분 업데이트를 지원합니다. 보낸 필드만 업데이트됩니다.

4. 모임 정보는 모임 하루 전부터 공개됩니다 (`isInfoRevealed: true`).

