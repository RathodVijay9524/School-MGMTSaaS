# 📄 Document Support for RAG System - Complete Guide

## 🎯 Overview

The School Management System now supports comprehensive document processing and intelligent retrieval through the RAG (Retrieval-Augmented Generation) system. Users can upload various document types, and the AI can intelligently search and retrieve information from these documents.

## 🚀 Features

### 📋 Supported File Types
- **PDF** - Academic papers, reports, policies, manuals
- **Word Documents** - Lesson plans, assignments, letters (.doc, .docx)
- **Excel Files** - Grade sheets, attendance records, budgets (.xls, .xlsx)
- **PowerPoint** - Lectures, training materials (.ppt, .pptx)

### 🔍 Intelligent Features
- **Automatic Text Extraction** - Extracts text content from all supported formats
- **Content Summarization** - Generates intelligent summaries of documents
- **Smart Search** - Search across document content, metadata, and tags
- **RAG Integration** - AI can reference uploaded documents in responses
- **Multi-tenant Support** - Document isolation by school/owner
- **Categorization** - Organize documents by type and purpose

## 📚 API Endpoints

### Document Upload
```http
POST /api/v1/documents/upload
Content-Type: multipart/form-data

Parameters:
- file: Document file (required)
- category: Document category (optional, default: "general")
- description: Document description (optional)
- tags: Comma-separated tags (optional)
```

### Bulk Upload
```http
POST /api/v1/documents/bulk-upload
Content-Type: multipart/form-data

Parameters:
- files: Multiple document files (required)
- category: Document category (optional)
- description: Document description (optional)
- tags: Comma-separated tags (optional)
```

### Document Search
```http
GET /api/v1/documents/search?query={search_term}&page=0&size=10
```

### Get Documents by Category
```http
GET /api/v1/documents/category/{category}?page=0&size=10
```

### Document Statistics
```http
GET /api/v1/documents/statistics
```

### Get Supported File Types
```http
GET /api/v1/documents/supported-types
```

## 🤖 MCP Tools Integration

### New RAG MCP Tools (8 Total)

1. **getIntelligentExamInfo** - Enhanced exam information with document context
2. **getStudentAssistance** - Student help with document references
3. **getAdministrativeSupport** - Admin tasks with document guidance
4. **getAcademicGuidance** - Academic support with curriculum documents
5. **getPolicyInformation** - Policy queries with document search
6. **searchSchoolData** - Universal search across all data
7. **searchDocuments** - Document-specific search
8. **getFileManagementAssistance** - File organization help

### Document MCP Tools

1. **uploadDocument** - Upload and process documents
2. **searchDocuments** - Search within documents
3. **getDocumentsForRAGQuery** - Get relevant documents for AI queries

## 📊 Document Categories

### Academic
- Lesson plans
- Curriculum materials
- Teaching resources
- Academic policies

### Administrative
- Reports
- Meeting minutes
- Administrative procedures
- Staff documents

### Student
- Student records
- Assignments
- Grade reports
- Student policies

### Teacher
- Training materials
- Resource guides
- Professional development
- Teaching tools

### Policy
- School policies
- Rules and regulations
- Procedures
- Guidelines

### Financial
- Budget documents
- Financial reports
- Fee structures
- Expense reports

### General
- Announcements
- Newsletters
- General information
- Miscellaneous documents

## 🔧 Technical Implementation

### Document Processing Pipeline

1. **Upload Validation**
   - File type validation
   - Size limit check (50MB)
   - Security scanning

2. **Text Extraction**
   - PDF: Apache PDFBox
   - Word: Apache POI
   - Excel: Apache POI
   - PowerPoint: Apache Tika

3. **Content Processing**
   - Text extraction
   - Summary generation
   - Metadata extraction
   - Tag assignment

4. **Storage**
   - File storage on disk
   - Database metadata
   - Full-text search indexing

5. **RAG Integration**
   - Content indexing
   - Query processing
   - Relevance scoring
   - Response generation

### Database Schema

```sql
CREATE TABLE documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    extracted_content TEXT,
    summary TEXT,
    owner_id BIGINT NOT NULL,
    category VARCHAR(100) NOT NULL DEFAULT 'general',
    tags VARCHAR(500),
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_processed BOOLEAN NOT NULL DEFAULT FALSE,
    processed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP NULL
);
```

## 🎯 Usage Examples

### Example 1: Upload Academic Policy
```bash
curl -X POST "http://localhost:9091/api/v1/documents/upload" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -F "file=@academic_policy.pdf" \
  -F "category=policy" \
  -F "description=Academic integrity policy for students" \
  -F "tags=policy,academic,integrity,students"
```

### Example 2: Search for Exam Information
```bash
curl -X GET "http://localhost:9091/api/v1/documents/search?query=exam%20schedule" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### Example 3: RAG Query with Document Context
```bash
curl -X POST "http://localhost:9091/api/v1/rag/query" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"query": "What are the academic policies for late submissions?"}'
```

## 🔐 Security & Permissions

### Role-Based Access
- **ROLE_ADMIN**: Full document management access
- **ROLE_TEACHER**: Upload, search, and manage documents
- **ROLE_STUDENT**: Search and view accessible documents

### File Security
- File type validation
- Size limits (50MB per file)
- Virus scanning (recommended)
- Access control by owner/school

### Data Privacy
- Multi-tenant isolation
- Document ownership tracking
- Soft delete functionality
- Audit logging

## 📈 Performance Considerations

### File Processing
- Asynchronous processing for large files
- Background processing queue
- Progress tracking
- Error handling and retry logic

### Search Optimization
- Full-text search indexing
- Cached search results
- Pagination support
- Relevance scoring

### Storage Management
- File compression
- Cleanup of deleted documents
- Storage quota management
- Backup and recovery

## 🚀 Getting Started

### 1. Upload Your First Document
```bash
# Login first
curl -X POST "http://localhost:9091/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail": "admin", "password": "admin"}'

# Upload document
curl -X POST "http://localhost:9091/api/v1/documents/upload" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -F "file=@your_document.pdf" \
  -F "category=academic" \
  -F "description=My first document"
```

### 2. Search Documents
```bash
curl -X GET "http://localhost:9091/api/v1/documents/search?query=policy" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 3. Use RAG with Documents
```bash
curl -X POST "http://localhost:9091/api/v1/rag/query" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"query": "What policies are mentioned in the uploaded documents?"}'
```

## 🔧 Configuration

### Application Properties
```properties
# Document upload directory
app.document.upload-dir=uploads/documents

# File size limits
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB

# Database configuration
spring.jpa.hibernate.ddl-auto=update
```

### Supported File Types
The system automatically detects and processes:
- PDF files (.pdf)
- Microsoft Word (.doc, .docx)
- Microsoft Excel (.xls, .xlsx)
- Microsoft PowerPoint (.ppt, .pptx)

## 📊 Monitoring & Analytics

### Document Statistics
- Total documents uploaded
- Documents by type
- Documents by category
- Processing status
- Storage usage

### RAG Performance
- Query response times
- Document relevance scores
- Search accuracy metrics
- User satisfaction ratings

## 🎉 Benefits

### For Schools
- **Centralized Knowledge Base** - All documents in one place
- **Intelligent Search** - Find information quickly
- **AI-Powered Assistance** - Get help with document content
- **Better Organization** - Categorize and tag documents
- **Multi-format Support** - Handle all common document types

### For Teachers
- **Easy Upload** - Simple document upload process
- **Smart Categorization** - Automatic document organization
- **Quick Access** - Find documents instantly
- **Content Search** - Search within document content
- **AI Integration** - Get intelligent responses about documents

### For Students
- **Document Access** - Access relevant documents
- **Smart Search** - Find information quickly
- **AI Help** - Get assistance with document content
- **Organized Resources** - Well-categorized document library

## 🚀 Future Enhancements

### Planned Features
- **OCR Support** - Extract text from images and scanned documents
- **Advanced Analytics** - Document usage analytics
- **Version Control** - Document versioning and history
- **Collaboration** - Document sharing and collaboration
- **Integration** - Integration with external document systems
- **AI Enhancement** - Advanced AI document analysis

### Technical Improvements
- **Cloud Storage** - Support for cloud storage providers
- **Real-time Processing** - Live document processing
- **Advanced Search** - Semantic search capabilities
- **Document Comparison** - Compare similar documents
- **Auto-categorization** - AI-powered document categorization

---

## 🎯 Summary

The Document Support feature transforms your School Management System into a comprehensive knowledge management platform. With support for multiple file types, intelligent text extraction, and AI-powered search, you can now:

1. **Upload** documents in various formats
2. **Search** across all document content
3. **Get AI assistance** with document-related queries
4. **Organize** documents by category and tags
5. **Access** documents through MCP tools
6. **Integrate** document knowledge with RAG system

**Total MCP Tools**: 286 (including 8 new RAG tools + 3 document tools)

Your School Management System is now a powerful document management and AI-assisted knowledge platform! 🎉
