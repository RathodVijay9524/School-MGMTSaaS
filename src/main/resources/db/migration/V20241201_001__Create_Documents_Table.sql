-- Create documents table for RAG system
CREATE TABLE IF NOT EXISTS documents (
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
    deleted_at TIMESTAMP NULL,
    
    INDEX idx_documents_owner_id (owner_id),
    INDEX idx_documents_category (category),
    INDEX idx_documents_file_type (file_type),
    INDEX idx_documents_created_at (created_at),
    INDEX idx_documents_is_processed (is_processed),
    INDEX idx_documents_deleted (deleted),
    FULLTEXT idx_documents_content (extracted_content, original_file_name, description, tags)
);

-- Add comments
ALTER TABLE documents COMMENT = 'Documents table for RAG system - stores uploaded documents with extracted content';
