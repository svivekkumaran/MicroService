/**
 * Configuration for Amazon Connect Chat Widget
 */
const CONFIG = {
    instanceId: '1d817c08-9ee8-40e6-8095-2e69d83a77fd',
    contactFlowId:'7a9b83f9-8f40-4cf7-abdf-b9930a896c86',
    surveyflowId: 'a621c71a-0b40-467e-97ff-bb1e08bce760',
    region: 'us-east-1',
    startChatUrl: 'https://yxkmxnqhh5k2d64snlrteey2qq0ergtb.lambda-url.us-east-1.on.aws',
    cstartChatUrl: 'https://tcsmecrrxn2zcm4cccgxoql7zq0vefqw.lambda-url.us-east-1.on.aws/',
    disconnectChatUrl: 'https://nabndyhup3altkvscotru34whu0nnyvl.lambda-url.us-east-1.on.aws/',
    scanAttachmentStatusUrl: "https://jeqdlevurknvckdpnw4sqbbvyy0wxpqh.lambda-url.us-east-1.on.aws/",
    chatAttachmentExtension: ['csv', 'doc', 'docx', 'heic', 'jfif', 'jpeg', 'jpg', 'mov', 'mp4', 'pdf', 'png', 'ppt', 'pptx', 'rtf', 'txt', 'wav', 'xls', 'xlsx'],
    maxAttachmentsPerSession: 10,
    maxAttachmentsAtOnce: 10,
    maxAttachmentSizeMB: 10
};

/**
 * Amazon Connect Chat Widget
 * 
 * This class handles:
 * 1. Chat session management with Amazon Connect
 * 2. Bot interaction with quick reply support
 * 3. Survey handling after chat ends
 */
class ConnectChatWidget {
    constructor() {
        // Core chat session object from Amazon Connect ChatJS
        this.chatSession = null;
        // Chat state management
        this.contactId = null;         // Unique identifier for this chat session
        this.isConnected = false;      // WebSocket connection status
        this.firstMessage = true;      // Track if this is customer's first message
        this.agentConnected = false;   // Track when human agent joins (vs bot)
        this.surveyMode = false;       // Track if currently in survey mode
        this.surveyCompleted = false;  // Track if survey is completed
        this.agentName = 'Agent';      // Track agent name dynamically
        this.agentEndedChat = false;   // Track if agent ended the conversation
        this.startingSurvey = false;   // Track if starting survey to prevent loops
        this.surveyPromptShown = false; // Track if survey prompt was already shown
        this.supervisorConnected = false; // Track if supervisor is connected
        this.supervisorName = 'Supervisor'; // Track supervisor name
        this.uploadCount = 0;          // Track number of uploads in session
        this.maxUploads = CONFIG.maxAttachmentsPerSession;           // Maximum uploads per session
        this.sessionUploadSize = 0;    // Track total size uploaded in session
        this.firstBotMessage = true;   // Track first bot message for centering
        this.selectedFiles = [];       // Store selected files for upload
        this.autoDisconnected = false; // Track if auto-disconnection occurred
        this.persistedChatSession = null; // Track persisted chat session
        this.transcriptRetrieved = false; // Track if transcript was retrieved
        this.storedTranscript = [];  // Store transcript for display
        this.firstCustomerMessageSent = false; // Track first customer message
        this.capturingInitialMessages = false; // Track initial message capture
        this.customContactAttributes = null; // Store custom contact attributes
        this.currentQueuePosition = ''; // Store current queue position
        this.currentWaitTime = ''; // Store current wait time
        this.nextToken = null; // Store next token for pagination
        this.loadingMoreMessages = false; // Track if loading more messages
        this.displayedMessageIds = new Set(); // Track displayed messages to prevent duplicates
        this.attachmentIds = []; // Store attachment IDs for bulk scanning
        this.attachmentBatches = new Map(); // Track multiple attachment batches
        // Initialize survey handler
        this.surveyHandler = new SurveyHandler(this);
        // Initialize chat UI
        this.initializeChatUI();
        // Initialize UI event handlers after HTML is injected
        this.initializeEventListeners();
        // Check for existing chat session on page load
        this.checkForExistingSession();
    }

    /**
     * Initialize chat UI by injecting HTML
     */
    initializeChatUI() {
        const chatHTML = `
            <!-- Chat Icon -->
            <div id="chat-icon" style="position: fixed; bottom: 20px; right: 20px; cursor: pointer; z-index: 1000;">
                <img src="https://d18k555lpxvevo.cloudfront.net/MNR/Portal/chaticon.png" alt="Chat" width="50" height="50">
                <div class="chat-tooltip" id="chat-tooltip">I can help you! Let's chat</div>
            </div>
            
            <!-- Chat Container (initially hidden) -->
            <div class="chat-container" id="chat-container" style="display: none;">
                <!-- Header Section -->
                <div class="chat-header">
                    <button class="menu-btn" id="menu-btn" aria-label="Menu">⋯</button>
                    <h2>UHC Chat</h2>
                    <div class="header-buttons">
                        <button class="minimize-chat" id="minimize-chat" aria-label="Minimize chat">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <polyline points="6 9 12 15 18 9"/>
                            </svg>
                        </button>
                    </div>
                </div>
                
                <!-- Menu Popup -->
                <div class="menu-popup" id="menu-popup" style="display: none;">
                    <div class="menu-item" id="print-transcript">
                        <span class="menu-icon">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                                <path d="M19 8H5c-1.66 0-3 1.34-3 3v6h4v4h12v-4h4v-6c0-1.66-1.34-3-3-3zm-3 11H8v-5h8v5zm3-7c-.55 0-1-.45-1-1s.45-1 1-1 1 .45 1 1-.45 1-1 1zm-1-9H6v4h12V3z"/>
                            </svg>
                        </span>
                        <span>Print Transcript</span>
                    </div>
                    <div class="menu-item" id="end-conversation">
                        <span class="menu-icon">
                            <svg width="16" height="16" viewBox="0 0 32 32" fill="#000000">
                                <line style="fill:none;stroke:#000000;stroke-width:2;stroke-miterlimit:10;" x1="16" y1="16" x2="16" y2="4"></line>
                                <path d="M20,4.703v2.141c3.526,1.547,6,5.064,6,9.155c0,5.514-4.486,10-10,10S6,21.514,6,16c0-4.091,2.474-7.609,6-9.155V4.703 C7.346,6.354,4,10.787,4,16c0,6.617,5.383,12,12,12s12-5.383,12-12C28,10.787,24.654,6.354,20,4.703z"></path>
                            </svg>
                        </span>
                        <span>End Conversation</span>
                    </div>
                </div>
                
                <!-- Chat Area -->
                <div class="chat-messages" id="chat-messages">
                    <!-- Suggested buttons will appear inline here -->
                </div>
                
                <!-- Typing Indicator -->
                <div id="typing-indicator" class="typing-indicator" style="display: none;">
                    <div class="typing-dots">
                        <span></span>
                        <span></span>
                        <span></span>
                    </div>
                    <span class="typing-text">Agent is typing...</span>
                </div>
                
                <!-- Input Section -->
                <div class="attachment-error" id="attachment-error" style="display: none;">
                    <div class="error-content">
                        <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 4px;">
                            <div class="error-icon">!</div>
                            <div class="error-title">Attachment Failed</div>
                        </div>
                        <div class="error-message" style="margin-left: 20px;">Up to 4 attachments allowed per session</div>
                    </div>
                    <button class="error-close" id="error-close" aria-label="Close error">×</button>
                </div>
                <div class="chat-input-container">
                    <button id="attach-btn" disabled aria-label="Attach file">
                        <svg width="35" height="35" viewBox="0 0 32 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M15.5 30C13.9667 30 12.6667 29.4667 11.6 28.4C10.5333 27.3333 10 26.0333 10 24.5V14C10 12.9 10.3917 11.9583 11.175 11.175C11.9583 10.3917 12.9 10 14 10C15.1 10 16.0417 10.3917 16.825 11.175C17.6083 11.9583 18 12.9 18 14V23.5C18 24.2 17.7583 24.7917 17.275 25.275C16.7917 25.7583 16.2 26 15.5 26C14.8 26 14.2083 25.7583 13.725 25.275C13.2417 24.7917 13 24.2 13 23.5V14.75C13 14.5333 13.0708 14.3542 13.2125 14.2125C13.3542 14.0708 13.5333 14 13.75 14C13.9667 14 14.1458 14.0708 14.2875 14.2125C14.4292 14.3542 14.5 14.5333 14.5 14.75V23.5C14.5 23.7833 14.5958 24.0208 14.7875 24.2125C14.9792 24.4042 15.2167 24.5 15.5 24.5C15.7833 24.5 16.0208 24.4042 16.2125 24.2125C16.4042 24.0208 16.5 23.7833 16.5 23.5V14C16.5 13.3 16.2583 12.7083 15.775 12.225C15.2917 11.7417 14.7 11.5 14 11.5C13.3 11.5 12.7083 11.7417 12.225 12.225C11.7417 12.7083 11.5 13.3 11.5 14V24.5C11.5 25.6 11.8917 26.5417 12.675 27.325C13.4583 28.1083 14.4 28.5 15.5 28.5C16.6 28.5 17.5417 28.1083 18.325 27.325C19.1083 26.5417 19.5 25.6 19.5 24.5V14.75C19.5 14.5333 19.5708 14.3542 19.7125 14.2125C19.8542 14.0708 20.0333 14 20.25 14C20.4667 14 20.6458 14.0708 20.7875 14.2125C20.9292 14.3542 21 14.5333 21 14.75V24.5C21 26.0333 20.4667 27.3333 19.4 28.4C18.3333 29.4667 17.0333 30 15.5 30Z" fill="currentColor"/>
                        </svg>
                    </button>
                    <div class="input-wrapper">
                        <div class="file-thumbnails" id="file-thumbnails"></div>
                        <input type="text" id="chat-input" placeholder="Ask your question" disabled aria-label="Ask your question">
                    </div>
                    <button id="send-btn" disabled aria-label="Send message">
                        <svg width="22" height="22" viewBox="0 0 24 24" fill="currentColor">
                            <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
                        </svg>
                    </button>
                    <input type="file" id="file-input" style="display: none;" accept=".csv,.doc,.docx,.heic,.jfif,.jpeg,.jpg,.mov,.mp4,.pdf,.png,.ppt,.pptx,.rtf,.txt,.wav,.xls,.xlsx" multiple>
                </div>
                
                <!-- Hidden controls for initialization -->
                <div class="chat-controls" style="display: none;">
                    <button id="start-chat-btn">Start Chat</button>
                    <button id="end-chat-btn" disabled>End Chat</button>
                    <div class="status" id="status">Ready to start chat</div>
                </div>
                
                <!-- Confirmation Modal -->
                <div class="confirmation-modal" id="confirmation-modal" style="display: none;">
                    <div class="confirmation-content">
                        <button class="modal-close-btn" id="modal-close-btn">×</button>
                        <h2>End conversation</h2>
                        <p>Are you sure you want to end this conversation?</p>
                        <div class="confirmation-buttons">
                            <button id="confirm-yes" class="confirm-btn yes">End conversation</button>
                            <button id="confirm-no" class="confirm-btn no">Keep chatting</button>
                        </div>
                    </div>
                </div>
                
                <!-- Survey Prompt Modal -->
                <div class="confirmation-modal" id="survey-prompt" style="display: none;">
                    <div class="confirmation-content">
                        <button class="modal-close-btn" id="survey-close-btn">×</button>
                        <h2>We value your feedback.</h2>
                        <p>Will you complete a short survey to let us know<br>how we're doing?</p>
                        <div class="confirmation-buttons">
                            <button id="survey-yes" class="confirm-btn yes">Yes</button>
                            <button id="survey-no" class="confirm-btn no">No</button>
                        </div>
                    </div>
                </div>
                
                <!-- Feedback Form Modal -->
                <div class="confirmation-modal" id="feedback-form" style="display: none;">
                    <div class="feedback-content">
                        <h3>📝 Feedback</h3>
                        <div class="rating">
                            <label>Rate your experience:</label>
                            <div class="stars">
                                <span class="star" data-rating="1">⭐</span>
                                <span class="star" data-rating="2">⭐</span>
                                <span class="star" data-rating="3">⭐</span>
                                <span class="star" data-rating="4">⭐</span>
                                <span class="star" data-rating="5">⭐</span>
                            </div>
                        </div>
                        <textarea id="feedback-text" placeholder="Please share your feedback..."></textarea>
                        <button id="submit-feedback" class="confirm-btn yes">Submit Feedback</button>
                    </div>
                </div>
                
                <!-- Attachment Modal (outside chat container) -->
                <div class="attachment-modal" id="attachment-modal" style="display: none;">
                    <div class="attachment-content">
                        <button class="close-btn" id="close-attachment-modal">×</button>
                        <h2>Attach photo</h2>
                        <ul class="attachment-guidelines">
                            <li>Find a well-lit area</li>
                            <li>Hold your device steady</li>
                            <li>Ensure all corners of the documents are visible within the border and near the frame edges</li>
                            <li id="max-file-size">Maximum file size is loading...</li>
                            <li id="accepted-formats">Accepted format:- Loading...</li>
                        </ul>
                        <div class="file-drop-area">
                            <div class="drop-text">Drag your file here</div>
                            <div class="or-text">or</div>
                            <button id="browse-btn" class="browse-btn">Browse</button>
                            <div class="file-size-limit" id="file-size-limit">Max file size: loading...</div>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        document.body.insertAdjacentHTML('beforeend', chatHTML);

        // Add chat icon event listener
        document.getElementById('chat-icon').addEventListener('click', () => {
            document.getElementById('chat-container').style.display = 'block';
            document.getElementById('chat-icon').style.display = 'none';
            
            // Only start new chat if no active session exists
            if (!this.isConnected) {
                console.log('[ChatWidget] Chat icon clicked, checking if contact attributes are set');
                console.log('[ChatWidget] Custom contact attributes:', this.customContactAttributes);
                
                // Check if contact attributes are available
                if (!this.customContactAttributes) {
                    console.warn('[ChatWidget] Contact attributes not set. Please call setContactAttributes() before starting chat.');
                    // You may want to show an error message to the user here
                    alert('Please ensure you are logged in before starting chat.');
                    return;
                }
                
                setTimeout(() => {
                    document.getElementById('start-chat-btn').click();
                }, 500);
            }
        });
        
        document.getElementById('minimize-chat').addEventListener('click', () => {
            document.getElementById('chat-container').style.display = 'none';
            document.getElementById('chat-icon').style.display = 'block';
        });
        
        document.getElementById('menu-btn').addEventListener('click', function() {
            const menuPopup = document.getElementById('menu-popup');
            menuPopup.style.display = menuPopup.style.display === 'none' ? 'block' : 'none';
        });
        
        document.getElementById('print-transcript').addEventListener('click', async function() {
            const menuItem = this;
            const originalText = menuItem.querySelector('span:last-child').textContent;
            
            // Show loading state
            menuItem.querySelector('span:last-child').textContent = 'Loading...';
            menuItem.style.pointerEvents = 'none';
            
            try {
                await window.acChatWidget.printTranscript();
            } finally {
                // Restore original state
                menuItem.querySelector('span:last-child').textContent = originalText;
                menuItem.style.pointerEvents = 'auto';
                document.getElementById('menu-popup').style.display = 'none';
            }
        });
        
        document.getElementById('end-conversation').addEventListener('click', function() {
            document.getElementById('menu-popup').style.display = 'none';
            document.getElementById('confirmation-modal').style.display = 'flex';
        });
        
        // Close menu popup when clicking outside
        document.addEventListener('click', function(e) {
            const menuPopup = document.getElementById('menu-popup');
            const menuBtn = document.getElementById('menu-btn');
            if (!menuPopup.contains(e.target) && !menuBtn.contains(e.target)) {
                menuPopup.style.display = 'none';
            }
        });
        
        document.getElementById('confirm-yes').addEventListener('click', function() {
            document.getElementById('confirmation-modal').style.display = 'none';
            const chatWidget = window.acChatWidget;
            if (chatWidget) {
                chatWidget.clearChatAttachments();
                
                if (chatWidget.agentConnected) {
                    const messagesContainer = document.getElementById('chat-messages');
                    const notificationDiv = document.createElement('div');
                    notificationDiv.className = 'chat-notification';
                    notificationDiv.innerHTML = 'You have exited the chat';
                    messagesContainer.appendChild(notificationDiv);
                    requestAnimationFrame(() => {
                        messagesContainer.scrollTop = messagesContainer.scrollHeight;
                    });
                }
            }
            
            if (chatWidget && chatWidget.agentConnected && !chatWidget.surveyPromptShown) {
                chatWidget.surveyPromptShown = true;
                document.getElementById('survey-prompt').style.display = 'flex';
            } else {
                document.getElementById('chat-container').style.display = 'none';
                document.getElementById('chat-icon').style.display = 'flex';
                const endBtn = document.getElementById('end-chat-btn');
                if (!endBtn.disabled) {
                    endBtn.click();
                }
            }
        });
        
        document.getElementById('confirm-no').addEventListener('click', () => {
            document.getElementById('confirmation-modal').style.display = 'none';
        });
        
        // Close modal when clicking the X button
        document.getElementById('modal-close-btn').addEventListener('click', () => {
            document.getElementById('confirmation-modal').style.display = 'none';
        });
        
        // Close survey prompt when clicking the X button
        document.getElementById('survey-close-btn').addEventListener('click', () => {
            document.getElementById('survey-prompt').style.display = 'none';
        });
        
        document.getElementById('browse-btn').addEventListener('click', () => {
            document.getElementById('file-input').click();
        });
        
        document.getElementById('close-attachment-modal').addEventListener('click', () => {
            document.getElementById('attachment-modal').style.display = 'none';
        });
        
        // Drag and drop handlers
        const dropArea = document.querySelector('.file-drop-area');
        
        dropArea.addEventListener('dragover', function(e) {
            e.preventDefault();
            dropArea.style.backgroundColor = '#e3f2fd';
        });
        
        dropArea.addEventListener('dragleave', function(e) {
            e.preventDefault();
            dropArea.style.backgroundColor = '#f5f5f5';
        });
        
        dropArea.addEventListener('drop', function(e) {
            e.preventDefault();
            dropArea.style.backgroundColor = '#f5f5f5';
            const files = Array.from(e.dataTransfer.files);
            if (files.length > 0) {
                const fileInput = document.getElementById('file-input');
                fileInput.files = e.dataTransfer.files;
                fileInput.dispatchEvent(new Event('change'));
            }
        });
        
        // Error banner close functionality
        document.getElementById('error-close').addEventListener('click', function() {
            document.getElementById('attachment-error').style.display = 'none';
        });
        
        // Survey prompt event listeners
        document.getElementById('survey-yes').addEventListener('click', function() {
            document.getElementById('survey-prompt').style.display = 'none';
            
            // Start survey using the same logic as regular chat
            const chatWidget = window.acChatWidget;
            if (chatWidget && !chatWidget.startingSurvey) {
                // Set flag to prevent survey loop
                chatWidget.startingSurvey = true;
                // Disconnect current session before starting survey
                if (chatWidget.chatSession && chatWidget.isConnected) {
                    try {
                        chatWidget.chatSession.disconnectParticipant();
                    } catch (error) {
                        console.log('[ChatWidget] Error disconnecting before survey:', error);
                    }
                }
                // Small delay to ensure disconnect completes
                setTimeout(() => {
                    chatWidget.startSurvey();
                }, 500);
            }
        });
        
        document.getElementById('survey-no').addEventListener('click', function() {
            document.getElementById('survey-prompt').style.display = 'none';
            document.getElementById('chat-container').style.display = 'none';
            document.getElementById('chat-icon').style.display = 'flex';
            // End chat if active
            const endBtn = document.getElementById('end-chat-btn');
            if (!endBtn.disabled) {
                endBtn.click();
            }
        });
        
        // Feedback form event listeners
        document.getElementById('submit-feedback').addEventListener('click', () => {
            document.getElementById('feedback-form').style.display = 'none';
        });
        
        // Star rating functionality
        document.querySelectorAll('.star').forEach(star => {
            star.addEventListener('click', (e) => {
                const rating = e.target.getAttribute('data-rating');
                document.querySelectorAll('.star').forEach((s, index) => {
                    s.style.opacity = index < rating ? '1' : '0.3';
                });
            });
        });
    }

    /**
     * Format message content with bullet points and hyperlinks
     * @param {string} content - Raw message content
     * @returns {string} - Formatted HTML content
     */
    formatMessageContent(content) {
        if (!content) return '';
        
        // Check if content is JSON structured content and convert to readable text
        content = this.convertJsonToText(content);
        
        // Convert line breaks to <br> tags
        let formatted = content.replace(/\n/g, '<br>');
        
        // Add line breaks before numbered lists (1.) (2.) etc.
        //formatted = formatted.replace(/(\d+\.)\s*/g, '<br>$1 ');
        
        
        // Add line breaks before bullet points (only at start of line or after whitespace)
        formatted = formatted.replace(/(^|\s)([•·])\s*/gm, '$1<br>$2 ');
        formatted = formatted.replace(/(^|\s)(-)\s+/gm, '$1<br>$2 ');
        
        // Remove leading <br> if it exists
        formatted = formatted.replace(/^<br>/, '');
        
        // Convert URLs to clickable hyperlinks
        const urlRegex = /(https?:\/\/[^\s]+)/g;
        formatted = formatted.replace(urlRegex, '<a href="$1" target="_blank" rel="noopener noreferrer">$1</a>');
        
        // Convert email addresses to mailto links first
        const emailRegex = /\b([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,})\b/g;
        formatted = formatted.replace(emailRegex, '<a href="mailto:$1">$1</a>');
        
        // Convert standalone domains to hyperlinks (after emails are processed)
        formatted = formatted.replace(/(?<!href="mailto:[^"]*|href="https?:\/\/[^"]*|@)\b([a-zA-Z0-9.-]+\.[a-zA-Z]{2,})\b(?![^<]*<\/a>)/g, 
            '<a href="https://$1" target="_blank" rel="noopener noreferrer">$1</a>');
        
        return formatted;
    }

    /**
     * Set up all UI event listeners for chat interactions
     * Handles button clicks, keyboard input, and language selection
     */
    initializeEventListeners() {
        // Start chat button - initiates new chat session
        document.getElementById('start-chat-btn').addEventListener('click', () => this.startChat());
        
        // End chat button - terminates current chat session
        document.getElementById('end-chat-btn').addEventListener('click', () => this.endChat());
        
        // Attachment button - opens attachment modal
        document.getElementById('attach-btn').addEventListener('click', () => {
            document.getElementById('attachment-modal').style.display = 'flex';
        });
        
        // File input - handles multiple file selection
        document.getElementById('file-input').addEventListener('change', (e) => {
            const selectedFiles = Array.from(e.target.files);
    
            
            // Close attachment modal first
            document.getElementById('attachment-modal').style.display = 'none';
            
            // Validate batch before showing thumbnails
            const batchSize = selectedFiles.reduce((sum, file) => sum + file.size, 0);
            const maxBatchSize = CONFIG.maxAttachmentSizeMB * 1024 * 1024;
            
            if (batchSize > maxBatchSize) {
                this.showAttachmentError('Maximum file size: 10MB');
                e.target.value = ''; // Clear file input
                return;
            }
            
            if (selectedFiles.length > CONFIG.maxAttachmentsAtOnce) {
                this.showAttachmentError('Maximum upload limit: 10 files');
                e.target.value = ''; // Clear file input
                return;
            }
            
            if (this.uploadCount + selectedFiles.length > CONFIG.maxAttachmentsPerSession) {
                this.showAttachmentError('Maximum upload limit: 10 files');
                e.target.value = ''; // Clear file input
                return;
            }
            
            // Add new files to existing selection instead of replacing
            const files = selectedFiles;
            this.selectedFiles = [...this.selectedFiles, ...files]; // Append to existing files
            this.showFileThumbnails(files);
        });
        
        // Remove typing indicator for customer input - only show for agent
        
        // Send message button - sends typed message
        document.getElementById('send-btn').addEventListener('click', () => this.sendMessage());
        
        // Enter key in input field - alternative way to send message
        document.getElementById('chat-input').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.sendMessage();
        });

        // Handle page refresh/close - call end chat API
        const disconnectChat = async (event) => {
            const clientLoginId = this.getClientLoginId();
            
            if (this.chatSession && this.isConnected && clientLoginId) {
                try {
                    // Use sendBeacon for more reliable cleanup on page unload
                    const data = JSON.stringify({
                        contactId: this.contactId,
                        clientLoginId: clientLoginId
                    });
                    
                    if (navigator.sendBeacon) {
                        navigator.sendBeacon(
                            `${CONFIG.startChatUrl}/chat-session/end`,
                            new Blob([data], { type: 'application/json' })
                        );
                    } else {
                        // Fallback to fetch with keepalive
                        fetch(`${CONFIG.startChatUrl}/chat-session/end`, {
                            method: 'DELETE',
                            headers: { 'Content-Type': 'application/json' },
                            body: data,
                            keepalive: true
                        });
                    }
                    
                    // Disconnect WebSocket
                    this.chatSession.disconnectParticipant();
                } catch (error) {
                    console.error('[ChatWidget] Error ending chat on page unload:', error);
                }
            }
        };
        
        // Only add beforeunload to disconnect WebSocket and update database
        window.addEventListener('beforeunload', disconnectChat);
        
        // Update UI elements with config values on page load
        window.addEventListener('load', function() {
            // Update accepted formats from config
            const acceptedFormatsElement = document.getElementById('accepted-formats');
            if (acceptedFormatsElement && typeof CONFIG !== 'undefined' && CONFIG.chatAttachmentExtension) {
                const extensions = CONFIG.chatAttachmentExtension.map(ext => '.' + ext).join(', ');
                acceptedFormatsElement.textContent = 'Accepted format:- ' + extensions;
            }
            
            // Update file size limits from config
            const maxFileSizeElement = document.getElementById('max-file-size');
            if (maxFileSizeElement && typeof CONFIG !== 'undefined' && CONFIG.maxAttachmentSizeMB) {
                maxFileSizeElement.textContent = `Maximum file size is ${CONFIG.maxAttachmentSizeMB}MB`;
            }
            
            const fileSizeLimitElement = document.getElementById('file-size-limit');
            if (fileSizeLimitElement && typeof CONFIG !== 'undefined' && CONFIG.maxAttachmentSizeMB) {
                fileSizeLimitElement.textContent = `Max file size: ${CONFIG.maxAttachmentSizeMB} MB`;
            }
        });
    }

    /**
     * Check for existing chat session after page refresh
     */
    async checkForExistingSession() {
        console.log('[ChatWidget] === CHECKING FOR EXISTING SESSION ===');
        const clientLoginId = this.getClientLoginId();
        console.log('[ChatWidget] Client login ID:', clientLoginId);
        
        if (clientLoginId) {
            try {
                console.log('[ChatWidget] Making GET request to check for active session');
                const sessionData = await this.getActiveSession(clientLoginId);
                console.log('[ChatWidget] Session data received:', sessionData);
                
                if (sessionData) {
                    console.log('[ChatWidget] Found existing session, attempting reconnection');
                    // Try to reconnect to the existing session
                    try {
                        await this.reconnectChat(sessionData);
                        console.log('[ChatWidget] Successfully reconnected to existing session');
                        return;
                    } catch (reconnectError) {
                        console.error('[ChatWidget] Failed to reconnect to existing session:', reconnectError);
                        // If reconnection fails, clean up the session and allow new chat
                        await this.cleanupFailedSession(clientLoginId);
                    }
                } else {
                    console.log('[ChatWidget] No existing session found');
                }
            } catch (e) {
                console.error('[ChatWidget] Error checking existing session:', e);
            }
        } else {
            console.log('[ChatWidget] No client login ID available, skipping session check');
        }
        
        // No active session found or reconnection failed - show fresh chat icon
        console.log('[ChatWidget] Resetting to initial state');
        this.resetToInitialState();
    }

    /**
     * Get client login ID from contact attributes or sessionStorage
     */
    getClientLoginId() {
        // First try to get from contact attributes
        let clientLoginId = this.customContactAttributes?.CustomerInfo?.ENT_ClientLoginID?.username;
        
        // If not available, try sessionStorage (for page refresh scenarios)
        if (!clientLoginId) {
            clientLoginId = sessionStorage.getItem('clientLoginId');
            console.log('[ChatWidget] Retrieved client login ID from sessionStorage:', clientLoginId);
        }
        
        console.log('[ChatWidget] Getting client login ID:', clientLoginId);
        console.log('[ChatWidget] Custom contact attributes:', this.customContactAttributes);
        return clientLoginId;
    }

    /**
     * Get active session from database
     */
    async getActiveSession(clientLoginId) {
        console.log('[ChatWidget] === GET_ACTIVE_SESSION === clientLoginId:', clientLoginId);
        try {
            const url = `${CONFIG.startChatUrl}/chat-session/${clientLoginId}`;
            console.log('[ChatWidget] Making GET request to:', url);
            
            const response = await fetch(url, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            });
            
            console.log('[ChatWidget] Response status:', response.status);
            console.log('[ChatWidget] Response ok:', response.ok);
            
            if (response.ok) {
                const data = await response.json();
                console.log('[ChatWidget] Session data received:', data);
                return data;
            } else {
                const errorText = await response.text();
                console.log('[ChatWidget] Error response:', errorText);
            }
            return null;
        } catch (error) {
            console.error('[ChatWidget] Error getting active session:', error);
            return null;
        }
    }

    /**
     * Reconnect to existing chat using session data from database
     */
    async reconnectChat(sessionData) {
        try {
            // Validate session data before attempting reconnection
            if (!sessionData.contactId || !sessionData.participantId || !sessionData.participantToken) {
                throw new Error('Invalid session data - missing required fields');
            }
            
            this.chatSession = connect.ChatSession.create({
                chatDetails: {
                    contactId: sessionData.contactId,
                    participantId: sessionData.participantId,
                    participantToken: sessionData.participantToken
                },
                options: { region: CONFIG.region },
                type: connect.ChatSession.SessionTypes.CUSTOMER
            });
            
            this.setupChatHandlers();
            
            // Set a timeout for connection attempt
            const connectionPromise = this.chatSession.connect();
            const timeoutPromise = new Promise((_, reject) => 
                setTimeout(() => reject(new Error('Connection timeout')), 10000)
            );
            
            await Promise.race([connectionPromise, timeoutPromise]);
            
            // Restore UI state
            this.isConnected = true;
            this.contactId = sessionData.contactId;
            document.getElementById('chat-container').style.display = 'flex';
            document.getElementById('chat-icon').style.display = 'none';
            document.getElementById('end-chat-btn').disabled = false;
            this.enableInput(true);
            
            // Load transcript after reconnection
            setTimeout(() => this.loadTranscriptLazily(), 1000);
            
            console.log('[ChatWidget] Successfully reconnected to existing session');
            return true;
            
        } catch (e) {
            console.error('[ChatWidget] Error reconnecting:', e);
            throw e; // Re-throw to be handled by caller
        }
    }

    /**
     * Set contact attributes from JSON - completely replaces existing attributes
     * @param {Object} attributesJson - JSON object containing contact attributes
     */
    async setContactAttributes(attributesJson) {
        if (typeof attributesJson === 'object' && attributesJson !== null) {
            
            console.log('[ChatWidget] Contact attributes :', attributesJson);
            // Capture device and location info
            const collectedInfo = await this.captureInfo();
            
            // Merge collected info into PersonalInfo
            if (attributesJson.PersonalInfo) {
                attributesJson.PersonalInfo.ENT_DeviceNm = collectedInfo.deviceInfo.deviceName;
                attributesJson.PersonalInfo.ENT_DevicePlatformNm = collectedInfo.deviceInfo.devicePlatform;
                attributesJson.PersonalInfo.ENT_ContactCityNm = collectedInfo.locationInfo.city;
                attributesJson.PersonalInfo.ENT_ContactStateCD = collectedInfo.locationInfo.ContactStateCD;
                attributesJson.PersonalInfo.ENT_ContactCountryCD = collectedInfo.locationInfo.country;
                attributesJson.PersonalInfo.ENT_ISP = collectedInfo.locationInfo.ip;
            }
            
            this.customContactAttributes = attributesJson;
            
            // Persist client login ID for session management across page refreshes
            const clientLoginId = attributesJson?.CustomerInfo?.ENT_ClientLoginID?.username;
            if (clientLoginId) {
                sessionStorage.setItem('clientLoginId', clientLoginId);
                console.log('[ChatWidget] Persisted client login ID:', clientLoginId);
            }
            
            console.log('[ChatWidget] Contact attributes set with device info:', attributesJson);
        } else {
            console.error('[ChatWidget] Invalid attributes JSON provided');
        }
    }

    /**
     * Clean up failed session from database
     */
    async cleanupFailedSession(clientLoginId) {
        try {
            console.log('[ChatWidget] Cleaning up failed session for client:', clientLoginId);
            
            // Try DELETE with body first, then fallback to query parameter
            let response = await fetch(`${CONFIG.startChatUrl}/chat-session/end`, {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    clientLoginId: clientLoginId
                })
            });
            
            // If that doesn't work, try with query parameter
            if (!response.ok) {
                console.log('[ChatWidget] DELETE with body failed, trying query parameter');
                response = await fetch(`${CONFIG.startChatUrl}/chat-session/end?clientLoginId=${encodeURIComponent(clientLoginId)}`, {
                    method: 'DELETE',
                    headers: { 'Content-Type': 'application/json' }
                });
            }
            
            console.log('[ChatWidget] Cleanup response status:', response.status);
            const responseText = await response.text();
            console.log('[ChatWidget] Cleanup response:', responseText);
            
            console.log('[ChatWidget] Cleaned up failed session for client:', clientLoginId);
        } catch (error) {
            console.error('[ChatWidget] Error cleaning up failed session:', error);
        }
    }

    /**
     * Reset widget to initial state
     */
    resetToInitialState() {
        // Reset all state flags
        this.isConnected = false;
        this.contactId = null;
        this.chatSession = null;
        this.agentConnected = false;
        this.firstBotMessage = true;
        
        // Reset UI elements
        document.getElementById('chat-container').style.display = 'none';
        document.getElementById('chat-icon').style.display = 'block';
        document.getElementById('start-chat-btn').disabled = false;
        document.getElementById('end-chat-btn').disabled = true;
        document.getElementById('chat-messages').innerHTML = '';
        
        // Clear any stored session data but keep client login ID for session management
        sessionStorage.removeItem('greetingShown');
        // Note: We keep 'clientLoginId' in sessionStorage for session management
        
        this.updateStatus('Ready to start chat');
        console.log('[ChatWidget] Reset to initial state');
    }

    /**
     * Initiates a new chat session with Amazon Connect
     * 
     * Process:
     * 1. Validates ChatJS library is loaded
     * 2. Calls start-chat Lambda to create Connect contact
     * 3. Receives chat credentials (contactId, participantId, token)
     * 4. Creates ChatJS session with credentials
     * 5. Establishes WebSocket connection to Connect
     */
    async startChat() {
        try {
            console.log('[ChatWidget] startChat called');
            // Reset all state flags for new chat
            this.agentConnected = false;
            this.agentEndedChat = false;
            this.surveyMode = false;
            this.surveyCompleted = false;
            this.surveyPromptShown = false;
            this.supervisorConnected = false;
            this.uploadCount = 0;
            this.sessionUploadSize = 0;
            this.autoDisconnected = false;
            this.transcriptRetrieved = false;
            this.storedTranscript = [];
            this.firstCustomerMessageSent = false;
            // Check for existing active session before starting new chat
            const clientLoginId = this.getClientLoginId();
            if (clientLoginId) {
                try {
                    console.log('[ChatWidget] Checking for existing session before starting new chat');
                    const existingSession = await this.getActiveSession(clientLoginId);
                    if (existingSession) {
                        console.log('[ChatWidget] Found existing active session, cleaning up before starting new chat');
                        await this.cleanupFailedSession(clientLoginId);
                        // Small delay to ensure cleanup completes
                        await new Promise(resolve => setTimeout(resolve, 500));
                        console.log('[ChatWidget] Cleanup completed, proceeding with fresh chat creation');
                    }
                } catch (error) {
                    console.log('[ChatWidget] Error checking for existing session:', error);
                }
            }
            // Clear chat history for new chat
            document.getElementById('chat-messages').innerHTML = '';
            this.displayedMessageIds.clear(); // Clear duplicate tracking for new chat
            this.firstBotMessage = true;
            // Hide input and show loading spinner until first bot message
            const chatInput = document.getElementById('chat-input');
            const sendBtn = document.getElementById('send-btn');
            const inputContainer = document.querySelector('.chat-input-container');
            const messagesContainer = document.getElementById('chat-messages');
            
            // Hide input container
            inputContainer.style.display = 'none';
            
            // Show loading spinner in center of messages area
            const loadingDiv = document.createElement('div');
            loadingDiv.id = 'chat-loading';
            loadingDiv.style.cssText = 'display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; gap: 12px;';
            
            const spinner = document.createElement('div');
            spinner.style.cssText = 'width: 32px; height: 32px; border: 3px solid #e0e0e0; border-top: 3px solid #196ECF; border-radius: 50%; animation: spin 1s linear infinite;';
            
            const loadingText = document.createElement('div');
            loadingText.textContent = 'Loading Chat ...';
            loadingText.style.cssText = 'color: #666; font-size: 14px; font-weight: 500;';
            
            loadingDiv.appendChild(spinner);
            loadingDiv.appendChild(loadingText);
            messagesContainer.appendChild(loadingDiv);
            

            // Verify Amazon Connect ChatJS library is loaded
            if (typeof connect === 'undefined') {
                throw new Error('Amazon Connect ChatJS library not loaded');
            }
            // Update UI to show chat is starting
            this.updateStatus('Connecting...');
            document.getElementById('start-chat-btn').disabled = true;
            // Prepare contact attributes
            let contactAttributes;
            
            if (this.customContactAttributes) {
                // Use custom attributes completely
                contactAttributes = this.customContactAttributes;
            } else {
                // Use default attributes from contact attributes
                const personalInfo = this.customContactAttributes?.PersonalInfo || {};
                const loggedInUser = personalInfo.ENT_FromAddress || '';
                const firstName = personalInfo.ENT_ContactFirstNm || '';
                const lastName = personalInfo.ENT_ContactLastNm || '';
                const userFullName = `${firstName} ${lastName}`.trim();
                
                contactAttributes = {
                    ...this.customContactAttributes,
                    username: loggedInUser,
                    fullName: userFullName,
                    ContactEmail: loggedInUser,
                    FirstName: firstName,
                    LastName: lastName,
                    ENT_ContactLastNm: lastName,
                    ENT_FromAddress: loggedInUser,
                    ENT_ContactFirstNm: firstName
                };
            }
            contactAttributes.dateAndTime = new Date().toISOString();
            const requestBody = {
                instanceId: CONFIG.instanceId,
                contactFlowId: CONFIG.contactFlowId,
                attributes: contactAttributes
            };
            
            // Call start-chat Lambda function to create Amazon Connect contact
            const response = await fetch(CONFIG.startChatUrl, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(requestBody)
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.error('[ChatWidget] Lambda error response:', errorText);
                throw new Error(`HTTP ${response.status}: ${errorText}`);
            }

            // Extract chat session credentials from Lambda response
            const chatData = await response.json();
            this.contactId = chatData.contactId;
            this.persistedChatSession = chatData.persistedChatSession;

            // Create Amazon Connect ChatJS session with received credentials
            this.chatSession = connect.ChatSession.create({
                chatDetails: {
                    contactId: chatData.contactId,
                    participantId: chatData.participantId,
                    participantToken: chatData.participantToken
                },
                options: { region: CONFIG.region },
                type: connect.ChatSession.SessionTypes.CUSTOMER
            });

            // Set up message handlers and event listeners
            this.setupChatHandlers();
            
            // Establish WebSocket connection to Amazon Connect
            await this.chatSession.connect();
            // Enable end chat button now that chat is active
            document.getElementById('end-chat-btn').disabled = false;
            // Hide minimize button after chat starts
            document.getElementById('minimize-chat').style.display = 'block';
            

        } catch (error) {
            console.error('[ChatWidget] Failed to start chat:', error);
            this.updateStatus('Failed to start chat');
            document.getElementById('start-chat-btn').disabled = false;
        }
    }

    /**
     * Create persistent contact association for future chat sessions
     */
    async createPersistentContactAssociation(contactId, clientToken) {
        try {
            // Store contact association with user identifier
            const userKey = `contactHistory_${clientToken}`;
            const existingHistory = JSON.parse(localStorage.getItem(userKey) || '[]');
            existingHistory.push({
                contactId: contactId,
                timestamp: new Date().toISOString()
            });
            // Keep only last 5 contacts
            if (existingHistory.length > 5) {
                existingHistory.splice(0, existingHistory.length - 5);
            }
            localStorage.setItem(userKey, JSON.stringify(existingHistory));
            return contactId;
        } catch (error) {
            console.error('[ChatWidget] Error creating persistent contact association:', error);
        }
        return null;
    }

    /**
     * Set up ChatJS event handlers for real-time chat events
     * Handles incoming messages, connection status, and chat lifecycle
     */
    setupChatHandlers() {
        // Handle incoming messages from bot/agent
        this.chatSession.onMessage(async (message) => {
            // Handle attachment messages - only from agents
            if (message.data && message.data.Type === 'ATTACHMENT' && message.data.ParticipantRole === 'AGENT') {
                this.displayAttachmentMessage(message.data);
                return;
            }
            // Check for participant joined EVENT
            if (message.data && message.data.Type === 'EVENT' && message.data.ContentType === 'application/vnd.amazonaws.connect.event.participant.joined') {
                if (message.data.ParticipantRole === 'AGENT') {
                    const wasAgentConnected = this.agentConnected;
                    this.agentName = message.data.DisplayName || 'Agent';
                    this.agentConnected = true;
                    this.updateStatus('Connected.. Start typing');
                    // Update chat state when agent connects
                    if (this.autoDisconnected) {
                        this.clearAutoDisconnectionMessages();
                        this.autoDisconnected = false;
                    }
                    // Clear queue messages when agent connects
                    this.clearQueueMessages();
                    if (!wasAgentConnected) {
                        this.showAgentJoinedMessage();
                        // Enable attachment button when agent joins
                        const attachBtn = document.getElementById('attach-btn');
                        if (attachBtn) {
                            attachBtn.style.display = 'flex';
                            attachBtn.disabled = false;
                        }
                        // Show menu button when agent joins
                        const menuBtn = document.getElementById('menu-btn');
                        if (menuBtn) {
                            menuBtn.style.display = 'flex';
                        }
                    } else {
                        this.showAgentJoinedMessage(); // Show message for reconnected agent too
                    }
                } else if (message.data.ParticipantRole === 'SUPERVISOR') {
                    this.supervisorName = message.data.DisplayName || 'Supervisor';
                    this.supervisorConnected = true;
                    this.showSupervisorJoinedMessage();
                }
                return; // Don't display EVENT messages
            }
            
            // Check for chat ended EVENT
            if (message.data && message.data.Type === 'EVENT' && message.data.ContentType === 'application/vnd.amazonaws.connect.event.chat.ended') {
                this.isConnected = false;
                this.agentEndedChat = true;
                this.enableInput(false);
                this.updateStatus('Chat ended');
                // Show survey prompt if agent was connected
                if (this.agentConnected && !this.surveyPromptShown) {
                    this.surveyPromptShown = true;
                    setTimeout(() => {
                        const surveyPrompt = document.getElementById('survey-prompt');
                        if (surveyPrompt) surveyPrompt.style.display = 'flex';
                    }, 1000);
                }
                return; // Don't display EVENT messages
            }
            
            // Check for participant left EVENT
            if (message.data && message.data.Type === 'EVENT' && message.data.ContentType === 'application/vnd.amazonaws.connect.event.participant.left') {
                if (message.data.ParticipantRole === 'SUPERVISOR' && this.supervisorConnected) {
                    this.supervisorConnected = false;
                    this.showSupervisorLeftMessage();
                }
                return; // Don't display EVENT messages
            }
            
            // Handle agent messages
            if (message.data && message.data.ParticipantRole === 'AGENT' && message.data.Content) {
                // Extract agent name from DisplayName if available
                if (message.data.DisplayName) {
                    this.agentName = message.data.DisplayName;
                }
                // Show agent joined message on first agent message if not already connected
                if (!this.agentConnected) {
                    this.agentConnected = true;
                    this.updateStatus('Connected.. Start typing');
                    this.clearAutoDisconnectionMessages();
                    this.clearQueueMessages();
                    this.showAgentJoinedMessage();
                    // Enable attachment button when agent joins
                    const attachBtn = document.getElementById('attach-btn');
                    if (attachBtn) {
                        attachBtn.style.display = 'flex';
                        attachBtn.disabled = false;
                    }
                    // Show menu button when agent joins
                    const menuBtn = document.getElementById('menu-btn');
                    if (menuBtn) {
                        menuBtn.style.display = 'flex';
                    }
                }
                // Mark all customer messages as read when agent responds
                this.markAllCustomerMessagesAsRead();
            }
            
            // Handle supervisor messages
            if (message.data && message.data.ParticipantRole === 'SUPERVISOR' && message.data.Content) {
                // Extract supervisor name from DisplayName if available
                if (message.data.DisplayName) {
                    this.supervisorName = message.data.DisplayName;
                }
            }
            
            // Hide typing indicator on any agent message
            if (message.data && message.data.ParticipantRole === 'AGENT') {
                const typingIndicator = document.getElementById('typing-indicator');
                if (typingIndicator) typingIndicator.style.display = 'none';
                clearTimeout(this.agentTypingTimeout);
            }
            // Display the message in chat UI (with translation if needed)
            this.displayMessage(message);
        });

        // WebSocket connection established successfully
        this.chatSession.onConnectionEstablished(async () => {
            this.updateStatus('Connected.. Start typing');
            this.isConnected = true;
            this.enableInput(true); // Enable message input
            // Don't load transcript immediately - wait for first customer message
        });

        // Chat ended by agent or customer
        this.chatSession.onEnded(() => {
            this.isConnected = false;
            this.clearChatAttachments();
            this.enableInput(false);
            
            if (this.agentConnected && !this.surveyMode) {
                const messagesContainer = document.getElementById('chat-messages');
                const notificationDiv = document.createElement('div');
                notificationDiv.className = 'chat-notification';
                notificationDiv.innerHTML = `${this.agentName} disconnected the chat`;
                messagesContainer.appendChild(notificationDiv);
                requestAnimationFrame(() => {
                    messagesContainer.scrollTop = messagesContainer.scrollHeight;
                });
                
                if (!this.startingSurvey && !this.surveyPromptShown) {
                    this.surveyPromptShown = true;
                    setTimeout(() => {
                        document.getElementById('survey-prompt').style.display = 'flex';
                    }, 1000);
                }
            }
            this.updateStatus('Chat ended');
        });
        
        // Agent typing indicator
        this.chatSession.onTyping((event) => {
            const participantRole = event.data ? event.data.ParticipantRole : null;
            
            if (participantRole === 'AGENT') {
                const indicator = document.getElementById('typing-indicator');
                if (indicator) {
                    indicator.style.display = 'flex';
                }
                clearTimeout(this.agentTypingTimeout);
                this.agentTypingTimeout = setTimeout(() => {
                    if (indicator) indicator.style.display = 'none';
                }, 3000);
            }
        });
        
        // Auto-disconnection event handler
        this.chatSession.onAutoDisconnection((event) => {
            // Check if auto-disconnection is triggered by agent
            const participantRole = event.data?.ParticipantRole;
            if (this.agentConnected && participantRole === 'AGENT') {
                this.autoDisconnected = true;
                this.showAutoDisconnectionMessage();
                this.handleAutoDisconnection();
            }
        });
        
        // Participant idle event handler
        this.chatSession.onParticipantIdle((event) => {
            const participant = event.data?.ParticipantRole || 'Participant';
            // this.showIdleNotification(participant, true);
        });
        // Participant returned event handler
        this.chatSession.onParticipantReturned((event) => {
            const participant = event.data?.ParticipantRole || 'Participant';
            // this.showIdleNotification(participant, false);
        });
        
        // WebSocket connection completely broken
        this.chatSession.onConnectionBroken(() => {
            this.updateStatus('Chat disconnected');
            this.isConnected = false;
            this.enableInput(false);
            // Clear chat attachments from input box
            this.clearChatAttachments();
            // Reset UI to initial state
            const startBtn = document.getElementById('start-chat-btn');
            const endBtn = document.getElementById('end-chat-btn');
            if (startBtn) startBtn.disabled = false;
            if (endBtn) endBtn.disabled = true;
        });
    }

    /**
     * Send customer message
     */
    async sendMessage() {
        const input = document.getElementById('chat-input');
        const message = input.value.trim();
        const thumbnailsContainer = document.getElementById('file-thumbnails');
        const attachments = thumbnailsContainer.children;
        // Check if there are attachments or message
        if (!message && attachments.length === 0) {
            return;
        }
        if (!this.chatSession) {
            return;
        }
        try {
            // Send text message if present
            if (message) {
                this.displayCustomerMessage(message);

                // Remove greeting from first bot message after customer input
                const firstBotMessage = document.querySelector('.message.system h2');
                if (firstBotMessage) {
                    firstBotMessage.remove();
                }
                
                // Load transcript after first customer message
                if (!this.firstCustomerMessageSent && this.persistedChatSession) {
                    this.firstCustomerMessageSent = true;
                    setTimeout(() => this.loadTranscriptLazily(), 1000);
                }
                
                await this.chatSession.sendMessage({
                    message: message,
                    contentType: 'text/plain'
                });
                input.value = '';
            }
            
            // Send attachments if present
            if (attachments.length > 0) {
                if (this.selectedFiles.length === 0) {
                    console.error('No files found in selectedFiles despite thumbnails present');
                    return;
                }                
                // Check batch size only
                const batchSize = this.selectedFiles.reduce((sum, file) => sum + file.size, 0);
                const maxBatchSize = CONFIG.maxAttachmentSizeMB * 1024 * 1024; // 10MB in bytes                
                if (batchSize > maxBatchSize) {
                    this.showAttachmentError('Maximum file size: 10MB');
                    return;
                }                
                // Validate attachments before sending
                if (this.selectedFiles.length > CONFIG.maxAttachmentsAtOnce) {
                    this.showAttachmentError('Maximum upload limit: 10 files');
                    return;
                }                
                // Don't pre-validate extensions here - let individual file validation handle it
                // This allows proper error display per file during upload process                
                // Check session limit before uploading
                if (this.uploadCount + this.selectedFiles.length > CONFIG.maxAttachmentsPerSession) {
                    this.showAttachmentError('Maximum upload limit: 10 files');
                    return;
                }                
                // Clear thumbnails and selected files immediately
                const filesToUpload = [...this.selectedFiles]; // Copy array
                thumbnailsContainer.innerHTML = '';
                this.selectedFiles = [];
                document.getElementById('file-input').value = '';                
                // Remove has-attachments class when clearing files
                const inputWrapper = document.querySelector('.input-wrapper');
                inputWrapper.classList.remove('has-attachments');                
                // Sort files by size (smallest to largest)
                const sortedFiles = filesToUpload.sort((a, b) => a.size - b.size);                
                // Show files in straight line first
                this.showMultipleAttachmentsInLine(sortedFiles);
                this.uploadResults = [];                
                for (let i = 0; i < sortedFiles.length; i++) {
                    console.log('[ChatWidget] Uploading file:', i + 1, 'of', sortedFiles.length, sortedFiles[i].name);
                    const success = await this.handleFileUpload(sortedFiles[i], false, false, i);
                    this.uploadResults.push(success);
                }                
                // Don't show remaining uploads yet - wait for scan results                
                // Don't convert to grid yet - wait for scan results
                // Grid conversion will happen after scan results are processed
                
                // Send attachment IDs to scan URL after all uploads complete
                const batchId = this.currentAttachmentMessage?.dataset.batchId;
                const batch = this.attachmentBatches.get(batchId);
                if (batch && batch.attachmentIds.length > 0) {
                    this.scanAttachments(batchId);
                }
            }            

        } catch (error) {
            console.error('[ChatWidget] Failed to send message:', error);
        }
    }

    /**
     * Start survey flow using the same logic as startChat
     */
    async startSurvey() {
        try {
            console.log('[ChatWidget] startSurvey called');
            
            if (typeof connect === 'undefined') {
                throw new Error('Amazon Connect ChatJS library not loaded');
            }
            this.updateStatus('Starting survey...');            
            // Set survey mode IMMEDIATELY
            this.surveyMode = true;
            this.surveyCompleted = false;
            this.startingSurvey = false;
            console.log('[ChatWidget] Survey mode activated immediately');
            
            // Hide attach button during survey
            const attachBtn = document.getElementById('attach-btn');
            if (attachBtn) {
                attachBtn.style.display = 'none';
            }            
            // Get attributes from localStorage and previous chat
            const chatKVPs = JSON.parse(localStorage.getItem('chatKVPs') || '{}');
            const loggedInUser = localStorage.getItem('loggedInUser') || '';
            const userFullName = localStorage.getItem('userFullName') || '';            
            const surveyAttributes = {
                ...chatKVPs,
                username: loggedInUser,
                fullName: userFullName,
                ContactEmail: loggedInUser,
                FirstName: userFullName.split(' ')[0] || '',
                LastName: userFullName.split(' ')[1] || '',
                ENT_ContactLastNm: userFullName.split(' ')[1] || '',
                ENT_FromAddress: loggedInUser,
                ENT_ContactFirstNm: userFullName.split(' ')[0] || '',
                originalContactId: this.contactId || ''
            };
            
            // Call Lambda with survey flow ID
            const response = await fetch(CONFIG.startChatUrl, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    type: 'survey',
                    //instanceId: CONFIG.instanceId,
                    //contactFlowId: CONFIG.surveyflowId, // Use survey flow ID
                    attributes: surveyAttributes
                })
            });

            if (!response.ok) throw new Error(`HTTP ${response.status}`);

            const chatData = await response.json();
            console.log('[ChatWidget] Survey Lambda response:', chatData);
            this.contactId = chatData.contactId;
            // Create new ChatJS session for survey
            this.chatSession = connect.ChatSession.create({
                chatDetails: {
                    contactId: chatData.contactId,
                    participantId: chatData.participantId,
                    participantToken: chatData.participantToken
                },
                options: { region: CONFIG.region },
                type: connect.ChatSession.SessionTypes.CUSTOMER
            });

            this.setupChatHandlers();
            await this.chatSession.connect();            
            this.updateStatus('Survey started');
            document.getElementById('end-chat-btn').disabled = false;

        } catch (error) {
            console.error('[ChatWidget] Failed to start survey:', error);
            this.updateStatus('Failed to start survey');
        }
    }

    /**
     * Manually end the chat session (customer-initiated)
     * Disconnects from Amazon Connect and resets UI state
     */
    async endChat() {
        const clientLoginId = this.getClientLoginId();
        
        if (this.chatSession && this.isConnected && !this.agentEndedChat) {
            try {
                this.chatSession.disconnectParticipant(); // Close Connect session
            } catch (error) {
                console.log('[ChatWidget] Error disconnecting:', error);
            }
        }
        
        // Update database to mark session as ended
        if (clientLoginId && this.contactId) {
            try {
                console.log('[ChatWidget] Sending DELETE request:', {
                    url: `${CONFIG.startChatUrl}/chat-session/end`,
                    contactId: this.contactId,
                    clientLoginId: clientLoginId
                });
                
                await fetch(`${CONFIG.startChatUrl}/chat-session/end?contactId=${encodeURIComponent(this.contactId)}&clientLoginId=${encodeURIComponent(clientLoginId)}`, {
                    method: 'DELETE',
                    headers: { 'Content-Type': 'application/json' }
                });
                
                console.log('[ChatWidget] DELETE response status:', response.status);
                const responseText = await response.text();
                console.log('[ChatWidget] DELETE response body:', responseText);
                
            } catch (error) {
                console.error('[ChatWidget] Error updating chat session status:', error);
            }
        } else {
            console.log('[ChatWidget] Skipping DELETE request - missing data:', {
                clientLoginId: !!clientLoginId,
                contactId: !!this.contactId
            });
        }
        
        this.updateStatus('Chat ended');
        this.isConnected = false;
        this.enableInput(false); // Disable message input
        
        // Clear chat attachments from input box
        this.clearChatAttachments();
        
        // Clear attachment IDs array
        this.attachmentIds = [];
        
        // Reset button states
        document.getElementById('start-chat-btn').disabled = false;
        document.getElementById('end-chat-btn').disabled = true;
    }

    /**
     * Display customer's own message in chat window
     * Shows original message in customer's language (no translation)
     */
    displayCustomerMessage(message) {
        const messagesContainer = document.getElementById('chat-messages');
        

        
        // Remove centering after first customer message
        const centeredMessage = document.querySelector('.first-bot-message');
        if (centeredMessage) {
            centeredMessage.classList.remove('first-bot-message');
            centeredMessage.style.position = 'static';
            centeredMessage.style.transform = 'none';
        }
        
        // Create customer info (timestamp)
        const customerInfo = document.createElement('div');
        customerInfo.className = 'customer-info';
        const now = new Date();
        const timeString = now.toLocaleTimeString('en-US', { 
            hour: 'numeric', 
            minute: '2-digit', 
            hour12: true 
        });
        customerInfo.textContent = timeString;        
        const messageDiv = document.createElement('div');
        messageDiv.className = 'message customer'; // Blue styling for customer messages
        messageDiv.textContent = message;
        messagesContainer.appendChild(customerInfo);
        messagesContainer.appendChild(messageDiv);
        this.addReadStatus(messageDiv);
        requestAnimationFrame(() => {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });
    }

    /**
     * Display incoming messages from bot/agent
     * 
     * Message Flow:
     * 1. Skip system EVENT messages (participant joined/left)
     * 2. Skip customer messages (already displayed by displayCustomerMessage)
     * 3. Handle bot structured content (quick replies)
     * 4. Display in appropriate styling (agent vs system/bot)
     */
    async displayMessage(message) {        
        // Skip EVENT messages (like participant joined/left notifications)
        if (message.data && message.data.Type === 'EVENT') {
            console.log('[ChatWidget] Skipping EVENT message:', message.data.ContentType);
            return;
        }        
        // Extract message content and sender info from ChatJS structure
        const messageData = message.data || message;
        let content = messageData.Content || messageData.content || messageData.text || 'No content';        
        // Skip messages with no content or "No content"
        if (!content || content.trim() === '' || content.toLowerCase().includes('no content')) {
            console.log('[ChatWidget] Skipping empty or no content message');
            return;
        }
        const participantRole = messageData.ParticipantRole || messageData.participantRole || 'unknown';
        
        // Skip customer messages (already displayed when sent via displayCustomerMessage)
        if (participantRole === 'CUSTOMER') {
            return;
        }
        
        // Check if structured content before adding greeting
        const isStructured = this.isStructuredContent(content);        
        const messagesContainer = document.getElementById('chat-messages');
        
        // Store if this is first bot message before any processing
        const isFirstBotMessage = this.firstBotMessage && participantRole !== 'CUSTOMER' && participantRole !== 'AGENT';
        
        // Add date separator before first bot message only if there are existing customer/agent messages
        if (isFirstBotMessage && messagesContainer.children.length > 0) {
            // Check if there are any customer or agent messages already
            const hasCustomerOrAgentMessages = Array.from(messagesContainer.children).some(child => {
                return child.classList && (child.classList.contains('customer-info') || 
                       child.querySelector && (child.querySelector('.message.customer') || child.querySelector('.message.agent')));
            });
            
            if (hasCustomerOrAgentMessages) {
                const today = new Date().toDateString();
                const dateSeparator = document.createElement('div');
                dateSeparator.className = 'date-separator';
                dateSeparator.textContent = 'Today';
                dateSeparator.style.cssText = 'text-align: center; color: #666; font-size: 12px; font-weight: bold; margin: 15px 0 10px 0; padding: 5px;';
                messagesContainer.appendChild(dateSeparator);
            }
        }
        
        // Enable input after first bot message
        if (isFirstBotMessage) {
            // Remove loading spinner and show input container
            const loadingDiv = document.getElementById('chat-loading');
            if (loadingDiv) {
                loadingDiv.remove();
            }
            
            const inputContainer = document.querySelector('.chat-input-container');
            inputContainer.style.display = 'flex';
            
            const chatInput = document.getElementById('chat-input');
            const sendBtn = document.getElementById('send-btn');
            chatInput.disabled = false;
            chatInput.readOnly = false;
            chatInput.placeholder = 'Ask your question';
            sendBtn.disabled = false;
        }
        
        // Add H2 greeting to first bot message content (only for non-structured content and once per session)        
        if (isFirstBotMessage && !isStructured && !sessionStorage.getItem('greetingShown')) {
            const personalInfo = this.customContactAttributes?.PersonalInfo || {};
            const firstName = personalInfo.ENT_ContactFirstNm || '';
            const lastName = personalInfo.ENT_ContactLastNm || '';
            const userFullName = `${firstName} ${lastName}`.trim() || 'User';
            content = `<h2 style="margin: 0 0 10px 0; font-size: 1.5em; font-weight: bold; color: #333;">Hi ${userFullName}</h2>${content}`;
            sessionStorage.setItem('greetingShown', 'true');            
        }
        
        // Display attachment if message type is attachment from agent
        if (messageData.Type === 'ATTACHMENT' && messageData.ParticipantRole === 'AGENT') {
            this.displayAttachmentMessage(messageData);
            return;
        }        
        // Handle bot structured content (QuickReply buttons, cards, etc.)
        if (isStructured) {
            this.displayStructuredMessage(content, participantRole, isFirstBotMessage);
            return;
        }        
        // Check for queue messages and display with spinner
        const queuePattern1 = /You are currently \d+ in queue/i;
        const queuePattern2 = /Your estimated wait time is \d+ to \d+ minutes/i;
        
        if (queuePattern1.test(content) || queuePattern2.test(content)) {
            this.updateQueueMessage(content);
            return;
        }
        
        // Check if this is survey content and enable survey mode
        if (content.toLowerCase().includes('survey') || content.toLowerCase().includes('feedback') || content.toLowerCase().includes('rate') || content.toLowerCase().includes('satisfaction')) {
            this.surveyMode = true;
            console.log('[ChatWidget] Survey mode activated');
        }        
        // Keep survey mode active for all subsequent messages until completion
        if (this.surveyMode && !this.surveyCompleted) {
            console.log('[ChatWidget] Continuing in survey mode');
        }        
        // Check if survey is completed
        if (this.surveyMode && (content.toLowerCase().includes('thank you for completing the survey!') || content.toLowerCase().includes('filling the survey'))) {
            this.surveyCompleted = true;
            this.surveyMode = false;
            this.agentConnected = false;
            this.agentEndedChat = false;
            this.updateStatus('Survey completed - Chat will close');
            this.enableInput(false);
            setTimeout(() => {
                this.clearChatAttachments();
                document.getElementById('chat-messages').innerHTML = '';
                this.clearSessionData();
                document.getElementById('chat-container').style.display = 'none';
                document.getElementById('chat-icon').style.display = 'flex';
                document.getElementById('start-chat-btn').disabled = false;
                document.getElementById('end-chat-btn').disabled = true;
            }, 2000);
        }        
        const messageDiv = document.createElement('div');        
        // Apply appropriate CSS styling based on sender
        const messageClass = participantRole === 'AGENT' ? 'agent' : 
                           participantRole === 'SUPERVISOR' ? 'supervisor' : 'system';
        
        // Create message wrapper for bot messages with separate icon
        if (messageClass === 'system') {            
            const messageWrapper = document.createElement('div');            
            // Apply centering only to first bot message
            if (this.firstBotMessage) {                
                messageWrapper.className = 'message-wrapper first-bot-message';
                this.firstBotMessage = false;
                messagesContainer.classList.add('has-messages');
            } else {                
                messageWrapper.className = 'message-wrapper';
            }            
            const iconDiv = document.createElement('div');
            iconDiv.className = 'bot-icon';
            iconDiv.textContent = '🤖';            
            messageDiv.className = `message ${messageClass}`;
            if (isFirstBotMessage) {
                messageDiv.innerHTML = this.formatMessageContent(content);
            } else {
                messageDiv.innerHTML = this.formatMessageContent(content);
            }            
            messageWrapper.appendChild(iconDiv);
            messageWrapper.appendChild(messageDiv);
            messagesContainer.appendChild(messageWrapper);
        } else if (messageClass === 'agent') {
            // Create agent message with icon
            const messageWrapper = document.createElement('div');
            messageWrapper.className = 'message-wrapper agent-wrapper';            
            const agentIcon = document.createElement('div');
            agentIcon.className = 'agent-icon';
            // Use dynamic agent name
            agentIcon.textContent = this.agentName.charAt(0).toUpperCase();            
            // Create agent info (name + timestamp)
            const agentInfo = document.createElement('div');
            agentInfo.className = 'agent-info';
            const now = new Date();
            const timeString = now.toLocaleTimeString('en-US', { 
                hour: 'numeric', 
                minute: '2-digit', 
                hour12: true 
            });
            agentInfo.textContent = `${this.agentName} ${timeString}`;            
            messageDiv.className = `message ${messageClass}`;
            messageDiv.innerHTML = this.formatMessageContent(content);            
            const messageContent = document.createElement('div');
            messageContent.className = 'message-content';
            messageContent.appendChild(agentInfo);
            messageContent.appendChild(messageDiv);            
            messageWrapper.appendChild(agentIcon);
            messageWrapper.appendChild(messageContent);
            messagesContainer.appendChild(messageWrapper);
        } else if (messageClass === 'supervisor') {
            // Create supervisor message with icon
            const messageWrapper = document.createElement('div');
            messageWrapper.className = 'message-wrapper supervisor-wrapper';            
            const supervisorIcon = document.createElement('div');
            supervisorIcon.className = 'supervisor-icon';
            // Use dynamic supervisor name
            supervisorIcon.textContent = this.supervisorName.charAt(0).toUpperCase();            
            // Create supervisor info (name + timestamp)
            const supervisorInfo = document.createElement('div');
            supervisorInfo.className = 'supervisor-info';
            const now = new Date();
            const timeString = now.toLocaleTimeString('en-US', { 
                hour: 'numeric', 
                minute: '2-digit', 
                hour12: true 
            });
            supervisorInfo.textContent = `${this.supervisorName} ${timeString}`;            
            messageDiv.className = `message supervisor`;
            messageDiv.innerHTML = this.formatMessageContent(content);            
            const messageContent = document.createElement('div');
            messageContent.className = 'message-content';
            messageContent.appendChild(supervisorInfo);
            messageContent.appendChild(messageDiv);            
            messageWrapper.appendChild(supervisorIcon);
            messageWrapper.appendChild(messageContent);
            messagesContainer.appendChild(messageWrapper);
        } else {
            messageDiv.className = `message ${messageClass}`;
            messageDiv.innerHTML = this.formatMessageContent(content);
            messagesContainer.appendChild(messageDiv);
        }        
        console.log('[ChatWidget] Adding message to UI:', { content, participantRole, messageClass });
        

        
        // Auto-scroll to bottom
        requestAnimationFrame(() => {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });
    }

    isStructuredContent(content) {
        try {
            // Create a temporary div to decode HTML entities
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = content;
            const decodedContent = tempDiv.textContent || tempDiv.innerText || '';            
            const parsed = JSON.parse(decodedContent);
            const isStructured = parsed.templateType === 'QuickReply' || parsed.templateType === 'ListPicker';            
            return isStructured;
        } catch (e) {            
            return false;
        }
    }

    displayStructuredMessage(content, participantRole, isFirstBotMessage = false) {
        try {
            // Decode HTML entities
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = content;
            const decodedContent = tempDiv.textContent || tempDiv.innerText || '';
            const structured = JSON.parse(decodedContent);            
            // Check if this is a survey question - use dropdown for ALL structured content in survey mode
            const messageContent = structured.data.content.title;
            if (this.surveyMode && structured.data.content.elements && !this.surveyCompleted) {
                this.surveyHandler.displaySurveyMessage(content, participantRole, isFirstBotMessage);
                return;
            }
            
            const messagesContainer = document.getElementById('chat-messages');            
            // Create wrapper for entire structured message
            const structuredWrapper = document.createElement('div');            
            if (this.firstBotMessage) {                
                structuredWrapper.className = 'structured-message-wrapper first-bot-message';
                this.firstBotMessage = false;
                messagesContainer.classList.add('has-messages');
            } else {
                structuredWrapper.className = 'structured-message-wrapper';
            }
            
            // Display main message with greeting if first bot message
            const messageDiv = document.createElement('div');
            messageDiv.className = `message ${participantRole === 'AGENT' ? 'agent' : 'system'}`;
            
            if (isFirstBotMessage) {
                console.log('[ChatWidget] Debug - customContactAttributes:', this.customContactAttributes);
                const personalInfo = this.customContactAttributes?.PersonalInfo || {};
                console.log('[ChatWidget] Debug - personalInfo:', personalInfo);
                const firstName = personalInfo.ENT_ContactFirstNm || '';
                const lastName = personalInfo.ENT_ContactLastNm || '';
                console.log('[ChatWidget] Debug - firstName:', firstName, 'lastName:', lastName);
                const userFullName = `${firstName} ${lastName}`.trim() || 'User';
                console.log('[ChatWidget] Debug - userFullName:', userFullName);
                const greetingContent = `<h2 style="margin: 0 0 10px 0; font-size: 1.5em; font-weight: bold; color: #333;">Hi ${userFullName}</h2>${messageContent}`;
                messageDiv.innerHTML = greetingContent;
            } else {
                messageDiv.textContent = messageContent;
            }
            
            structuredWrapper.appendChild(messageDiv);
            
            // Display options inline with messages
            if (structured.data.content.elements) {
                const optionsDiv = document.createElement('div');
                optionsDiv.className = 'inline-options';                
                if (structured.templateType === 'ListPicker') {
                    optionsDiv.classList.add('list-picker');
                    structured.data.content.elements.forEach(element => {
                        const option = document.createElement('button');
                        option.className = 'list-option';
                        option.textContent = element.title;
                        option.onclick = () => this.sendQuickReply(element.title);
                        optionsDiv.appendChild(option);
                    });
                } else {
                    // QuickReply buttons
                    optionsDiv.classList.add('quick-replies');
                    structured.data.content.elements.forEach(element => {
                        const button = document.createElement('button');
                        button.className = 'quick-reply-btn';
                        button.textContent = element.title;
                        button.onclick = () => this.sendQuickReply(element.title);
                        optionsDiv.appendChild(button);
                    });
                }                
                structuredWrapper.appendChild(optionsDiv);
            }            
            messagesContainer.appendChild(structuredWrapper);            
            requestAnimationFrame(() => {
                messagesContainer.scrollTop = messagesContainer.scrollHeight;
            });
        } catch (error) {
            console.error('[ChatWidget] Error displaying structured message:', error);
        }
    }

    sendQuickReply(text) {
        // Hide all inline options after selection
        const allOptions = document.querySelectorAll('.inline-options');
        allOptions.forEach(option => option.style.display = 'none');        
        // Remove greeting from first message after quick reply selection
        const firstMessage = document.querySelector('.message.system');
        if (firstMessage && firstMessage.innerHTML.includes('<h2')) {
            const h2Element = firstMessage.querySelector('h2');
            if (h2Element) {
                h2Element.remove();
            }
        }        
        // Send the selected option directly without showing in input
        this.sendQuickReplyDirect(text);
    }

    async sendQuickReplyDirect(message) {
        if (!this.chatSession) return;

        try {
            // Show customer message immediately
            this.displayCustomerMessage(message);

            // Load transcript after first customer response (including quick reply)
            if (!this.firstCustomerMessageSent && this.persistedChatSession) {
                this.firstCustomerMessageSent = true;
                setTimeout(() => this.loadTranscriptLazily(), 1000);
            }

            // Send message through Amazon Connect
            await this.chatSession.sendMessage({
                message: message,
                contentType: 'text/plain'
            });
        } catch (error) {
            console.error('[ChatWidget] Failed to send quick reply:', error);
        }
    }

    enableInput(enabled) {
        document.getElementById('chat-input').disabled = !enabled;
        document.getElementById('send-btn').disabled = !enabled;
        document.getElementById('attach-btn').disabled = !enabled;
    }
    
    async handleFileUpload(file, isLastFile = true, showIndividualMessage = true, fileIndex = null) {
        console.log('[ChatWidget] handleFileUpload called with file:', file?.name, 'size:', file?.size);        
        if (!file || !this.chatSession) {
            console.error('[ChatWidget] Missing file or chat session:', { file: !!file, chatSession: !!this.chatSession });
            return false;
        }        
        // Check session upload limit
        if (this.uploadCount >= this.maxUploads) {
            console.log('[ChatWidget] Upload limit reached:', this.uploadCount, '>=', this.maxUploads);
            this.showAttachmentError('Maximum upload limit: 10 files');
            return false;
        }        
        // Check file extension
        const fileExt = file.name.split('.').pop().toLowerCase();
        const supportedExtensions = CONFIG.chatAttachmentExtension;
        if (!supportedExtensions.includes(fileExt)) {
            console.log('[ChatWidget] Invalid file format:', fileExt);
            if (fileIndex !== null) {
                this.updateThumbnailState(fileIndex, 'format-error');
                this.showFileError(fileIndex, 'Invalid file format');
            }
            return false;
        }        
        // Check if file would exceed session size limit
        const maxSessionSize = CONFIG.maxAttachmentSizeMB * 1024 * 1024; // 10MB in bytes
        if (this.sessionUploadSize + file.size > maxSessionSize) {
            console.log('[ChatWidget] File would exceed session limit:', this.sessionUploadSize + file.size, '>', maxSessionSize);
            if (fileIndex !== null) {
                this.updateThumbnailState(fileIndex, 'size-error');
                this.showFileError(fileIndex, 'File size should be less than 10MB');
            }
            return false; // Don't upload but show exclamation
        }        
        const progressDiv = showIndividualMessage ? this.showUploadProgress(file.name, file) : null;        
        try {
            console.log('[ChatWidget] Attempting to send attachment via chatSession.sendAttachment');
            // Send attachment with all required parameters
            const attachmentResponse = await this.chatSession.sendAttachment({
                ContentType: file.type,
                AttachmentName: file.name,
                AttachmentSizeInBytes: file.size,
                attachment: file
            });            
            console.log('[ChatWidget] File attachment sent successfully:', attachmentResponse);
            
            // Extract attachment ID from response
            const attachmentId = attachmentResponse.request.params.AttachmentIds[0];
            if (attachmentId && this.currentAttachmentMessage) {
                const batchId = this.currentAttachmentMessage.dataset.batchId;
                const batch = this.attachmentBatches.get(batchId);
                if (batch) {
                    batch.attachmentIds.push(attachmentId);
                    console.log('[ChatWidget] Added attachment ID to batch:', attachmentId, batchId);
                }
            }
            
            // Hide loading indicator
            this.hideUploadProgress(progressDiv);            
            // Keep loading state until scan results come back
            if (fileIndex !== null) {
                // Don't update to success yet - wait for scan results
                console.log('[ChatWidget] Keeping loading state for file index:', fileIndex);
            }            
            // Don't increment upload count until scan results confirm acceptance
            // this.uploadCount++;
            // this.sessionUploadSize += file.size;            
            return true;
            
        } catch (error) {
            console.error('[ChatWidget] Failed to send attachment:', error);
            console.error('[ChatWidget] Error details:', error.message, error.stack);
            this.hideUploadProgress(progressDiv, true);            
            // Update thumbnail state to failed
            if (fileIndex !== null) {
                this.updateThumbnailState(fileIndex, 'failed');
            }            
            // Show error message in attachment error banner
            this.showAttachmentError('Attachment failed');
            return false;
        }
    }
    
    showUploadProgress(fileName, file) {
        const messagesContainer = document.getElementById('chat-messages');        
        const fileDiv = document.createElement('div');
        fileDiv.className = 'message customer file-message';        
        const thumbnailContainer = document.createElement('div');
        thumbnailContainer.className = 'file-thumbnail-chat';        
        this.createFileThumbnail(file, thumbnailContainer, true);        
        fileDiv.appendChild(thumbnailContainer);
        messagesContainer.appendChild(fileDiv);
        this.addReadStatus(fileDiv);        
        requestAnimationFrame(() => {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });        
        return fileDiv;
    }
    
    hideUploadProgress(progressDiv, failed = false) {
        if (progressDiv) {
            const loadingOverlay = progressDiv.querySelector('.file-loading-overlay');
            if (loadingOverlay) {
                if (failed) {
                    loadingOverlay.innerHTML = '<div style="color: red; font-size: 12px; font-weight: bold;">Failed</div>';
                    loadingOverlay.style.background = 'rgba(255,0,0,0.1)';
                } else {
                    loadingOverlay.style.display = 'none';
                }
            }
        }
    }
    
    createFileThumbnail(file, container, showLoading = false) {
        const fileExt = file.name.split('.').pop().toLowerCase();
        const isImage = ['heic', 'jfif', 'jpeg', 'jpg', 'png'].includes(fileExt);        
        const img = document.createElement('img');
        img.className = 'file-preview-image';
        
        if (isImage) {
            img.onload = () => URL.revokeObjectURL(img.src);
            img.src = URL.createObjectURL(file);
        } else {
            // Use a generic file icon for non-images
            img.src = this.getFileIconUrl(fileExt);
        }
        container.appendChild(img);
        
        if (showLoading) {
            const overlay = document.createElement('div');
            overlay.className = 'file-loading-overlay';
            overlay.innerHTML = '<div class="file-spinner"></div>';
            container.appendChild(overlay);
        }
    }    
    getFileIcon(ext) {
        const icons = {
            pdf: '📄',
            doc: '📝', docx: '📝',
            xls: '📊', xlsx: '📊',
            ppt: '📊', pptx: '📊',
            txt: '📄',
            zip: '🗜️', rar: '🗜️',
            mp4: '🎥', avi: '🎥', mov: '🎥',
            mp3: '🎵', wav: '🎵'
        };
        return icons[ext] || '📎';
    }
    
    getFileIconUrl(ext) {
        // Create a canvas to generate file type thumbnails
        const canvas = document.createElement('canvas');
        canvas.width = 64;
        canvas.height = 64;
        const ctx = canvas.getContext('2d');        
        // Background
        ctx.fillStyle = '#f0f0f0';
        ctx.fillRect(0, 0, 64, 64);        
        // Border
        ctx.strokeStyle = '#ddd';
        ctx.lineWidth = 1;
        ctx.strokeRect(0, 0, 64, 64);        
        // File icon
        ctx.font = '20px Arial';
        ctx.textAlign = 'center';
        ctx.fillStyle = '#666';
        ctx.fillText(this.getFileIcon(ext), 32, 30);        
        // Extension text
        ctx.font = 'bold 8px Arial';
        ctx.fillStyle = '#333';
        ctx.fillText(ext.toUpperCase(), 32, 50);        
        return canvas.toDataURL();
    }
    
    showFileSizeError(fileName) {
        const messagesContainer = document.getElementById('chat-messages');        
        // Show file name message
        const fileDiv = document.createElement('div');
        fileDiv.className = 'message customer';
        fileDiv.textContent = `📎 ${fileName}`;
        messagesContainer.appendChild(fileDiv);        
        // Show error message below
        const errorDiv = document.createElement('div');
        errorDiv.style.cssText = 'color: red; font-size: 12px; font-weight: bold; margin: 4px 0; text-align: right;';
        errorDiv.textContent = 'File size should be less than 10 MB';
        messagesContainer.appendChild(errorDiv);        
        requestAnimationFrame(() => {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });
    }
    
    showUploadLimitError() {
        const messagesContainer = document.getElementById('chat-messages');
        const errorDiv = document.createElement('div');
        errorDiv.style.cssText = 'color: red; font-size: 12px; font-weight: bold; margin: 4px 0; text-align: right;';
        errorDiv.textContent = `Maximum ${CONFIG.maxAttachmentsPerSession} attachments allowed per session`;
        messagesContainer.appendChild(errorDiv);
        requestAnimationFrame(() => {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });
    }    
    showSelectionLimitError() {
        console.log('[ChatWidget] showSelectionLimitError called');
        this.showAttachmentError(`Up to ${CONFIG.maxAttachmentsAtOnce} attachments allowed per session`);
    }    
    showExtensionError() {
        this.showAttachmentError('Extension not supported');
    }
    
    showChatError(message, color = 'red') {
        const messagesContainer = document.getElementById('chat-messages');
        const errorDiv = document.createElement('div');
        const fontWeight = (message.includes('File size should be less than 10MB') || message.includes('Invalid file format')) ? 'normal' : 'bold';
        errorDiv.style.cssText = `color: ${color}; font-size: 12px; font-weight: ${fontWeight}; margin: 4px 0; text-align: right;`;
        errorDiv.textContent = message;
        messagesContainer.appendChild(errorDiv);
        requestAnimationFrame(() => {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });
    }
    
    showFileError(fileIndex, message, messageDiv = null) {
        const targetMessage = messageDiv || this.currentAttachmentMessage;
        if (!targetMessage) return;
        
        const thumbnail = targetMessage.querySelector(`[data-file-index="${fileIndex}"]`);
        if (!thumbnail) return;
        
        const errorDiv = document.createElement('div');
        const isFormatError = message.includes('Invalid file format');
        const isMalwareError = message.includes('Potential Malware');
        let marginLeft;
        if (isFormatError) {
            marginLeft = '10px';
        } else if (isMalwareError) {
            marginLeft = '30px';
        } else {
            marginLeft = '-100px';
        }
        const marginBottom = (isFormatError || isMalwareError) ? '8px' : '0px';
        errorDiv.style.cssText = `color: #323334; font-size: 14px; font-family: "Enterprise Sans VF", Arial, sans-serif; font-weight: normal; margin-top: 2px; margin-bottom: ${marginBottom}; text-align: left; white-space: nowrap; width: 300px;`;
        errorDiv.textContent = message;
        thumbnail.parentNode.insertBefore(errorDiv, thumbnail.nextSibling);
    }
    
    showFileThumbnails(files) {
        const thumbnailsContainer = document.getElementById('file-thumbnails');
        const inputWrapper = document.querySelector('.input-wrapper');
        
        files.forEach((file, index) => {
            const thumbnail = document.createElement('div');
            thumbnail.className = 'file-thumbnail-preview';            
            // Create preview container
            const previewContainer = document.createElement('div');
            previewContainer.className = 'file-preview-container';            
            this.createFileThumbnail(file, previewContainer, false);            
            const removeBtn = document.createElement('button');
            removeBtn.className = 'remove-file';
            removeBtn.textContent = '×';
            removeBtn.onclick = () => {
                // Remove from selectedFiles array
                const fileIndex = this.selectedFiles.indexOf(file);
                if (fileIndex > -1) {
                    this.selectedFiles.splice(fileIndex, 1);
                }
                thumbnail.remove();
                this.hideAttachmentError();
                this.updateInputHeight();                
                // Remove has-attachments class if no more files
                if (this.selectedFiles.length === 0) {
                    inputWrapper.classList.remove('has-attachments');
                }
            };            
            thumbnail.appendChild(previewContainer);
            thumbnail.appendChild(removeBtn);
            thumbnailsContainer.appendChild(thumbnail);
        });        
        // Add has-attachments class when files are present
        if (this.selectedFiles.length > 0) {
            inputWrapper.classList.add('has-attachments');
        }        
        this.updateInputHeight();
    }
    
    showAttachmentError(message = `Maximum ${CONFIG.maxAttachmentsPerSession} attachments allowed per session`) {
        console.log('[ChatWidget] showAttachmentError called with message:', message);
        this.updateInputHeight(); // Update input height before showing error
        const errorBanner = document.getElementById('attachment-error');
        console.log('[ChatWidget] Error banner element:', errorBanner);
        if (errorBanner) {
            const errorMessage = errorBanner.querySelector('.error-message');
            console.log('[ChatWidget] Error message element:', errorMessage);
            if (errorMessage) {
                errorMessage.textContent = message;
            }
            errorBanner.style.display = 'flex';
            console.log('[ChatWidget] Error banner display set to flex');
        } else {
            console.error('[ChatWidget] Error banner element not found!');
        }
    }    
    hideAttachmentError() {
        const errorBanner = document.getElementById('attachment-error');
        errorBanner.style.display = 'none';
    }    
    updateInputHeight() {
        const inputContainer = document.querySelector('.chat-input-container');
        if (inputContainer) {
            const height = inputContainer.offsetHeight;
            document.documentElement.style.setProperty('--input-height', height + 'px');
        }
    }    
    showFileConfirmation(files) {
        const messagesContainer = document.getElementById('chat-messages');
        const confirmDiv = document.createElement('div');
        confirmDiv.className = 'file-confirmation';
        
        let fileList = files.map(file => `📎 ${file.name}`).join('<br>');
        confirmDiv.innerHTML = `
            <div>${fileList}</div>
            <div style="margin-top: 8px;">
                <button class="confirm-send">Send</button>
                <button class="confirm-cancel">Cancel</button>
            </div>
        `;
        
        messagesContainer.appendChild(confirmDiv);        
        confirmDiv.querySelector('.confirm-send').onclick = async () => {
            confirmDiv.remove();
            for (let i = 0; i < files.length; i++) {
                await this.handleFileUpload(files[i], i === files.length - 1);
            }
        };        
        confirmDiv.querySelector('.confirm-cancel').onclick = () => {
            confirmDiv.remove();
        };        
        requestAnimationFrame(() => {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });
    }
       
    showRemainingUploads() {
        const remaining = CONFIG.maxAttachmentsPerSession - this.uploadCount;
        if (remaining > 0) {
            const messagesContainer = document.getElementById('chat-messages');
            const counterDiv = document.createElement('div');
            counterDiv.className = 'message-status';
            counterDiv.textContent = `${remaining} attachment${remaining === 1 ? '' : 's'} left`;
            messagesContainer.appendChild(counterDiv);
            requestAnimationFrame(() => {
                messagesContainer.scrollTop = messagesContainer.scrollHeight;
            });
        }
    }
    
    displayFileMessage(fileData) {
        const messagesContainer = document.getElementById('chat-messages');
        const messageDiv = document.createElement('div');
        messageDiv.className = 'message customer file-message';
        messageDiv.innerHTML = `📎 ${fileData.name} (${(fileData.size/1024).toFixed(1)}KB)`;
        messagesContainer.appendChild(messageDiv);
        this.addReadStatus(messageDiv);
        requestAnimationFrame(() => {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });
    }
    
    displayAttachmentMessage(messageData) {
        const messagesContainer = document.getElementById('chat-messages');        
        const messageWrapper = document.createElement('div');
        messageWrapper.className = 'message-wrapper agent-wrapper';        
        const agentIcon = document.createElement('div');
        agentIcon.className = 'agent-icon';
        agentIcon.textContent = this.agentName.charAt(0).toUpperCase();        
        const messageContent = document.createElement('div');
        messageContent.className = 'message-content';        
        const agentInfo = document.createElement('div');
        agentInfo.className = 'agent-info';
        const now = new Date();
        const timeString = now.toLocaleTimeString('en-US', { 
            hour: 'numeric', 
            minute: '2-digit', 
            hour12: true 
        });
        agentInfo.textContent = `${this.agentName} ${timeString}`;
        messageContent.appendChild(agentInfo);        
        const messageDiv = document.createElement('div');
        messageDiv.className = 'message agent';
        messageDiv.style.width = '220px';        
        const attachmentContainer = document.createElement('div');
        attachmentContainer.className = 'agent-attachment-container';        
        messageData.Attachments.forEach(async (attachment) => {
            const attachmentItem = document.createElement('div');
            attachmentItem.className = 'agent-attachment-item';            
            const img = document.createElement('img');
            img.className = 'agent-attachment-preview';
            img.style.width = '160px';
            img.style.height = '160px';            
            if (attachment.ContentType.startsWith('image/')) {
                try {
                    const downloadResponse = await this.chatSession.downloadAttachment({
                        attachmentId: attachment.AttachmentId
                    });
                    const blob = new Blob([downloadResponse], { type: attachment.ContentType });
                    img.src = URL.createObjectURL(blob);
                } catch (error) {
                    img.src = this.getFileIconUrl('img');
                }
            } else {
                const ext = attachment.AttachmentName.split('.').pop().toLowerCase();
                img.src = this.getFileIconUrl(ext);
            }
            
            const downloadBtn = document.createElement('button');
            downloadBtn.className = 'agent-download-btn';
            downloadBtn.style.fontWeight = '900';
            downloadBtn.style.fontFamily = 'Arial';
            downloadBtn.textContent = 'Download';
            downloadBtn.onclick = () => this.downloadAttachment(attachment);            
            attachmentItem.appendChild(img);
            attachmentItem.appendChild(downloadBtn);
            attachmentContainer.appendChild(attachmentItem);
        });
        
        messageDiv.appendChild(attachmentContainer);
        messageContent.appendChild(messageDiv);
        messageWrapper.appendChild(agentIcon);
        messageWrapper.appendChild(messageContent);
        messagesContainer.appendChild(messageWrapper);        
        requestAnimationFrame(() => {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });
    }
    
    getAttachmentIcon(contentType) {
        if (contentType.startsWith('image/')) return '🖼️';
        if (contentType.includes('pdf')) return '📄';
        if (contentType.includes('word') || contentType.includes('document')) return '📝';
        if (contentType.includes('excel') || contentType.includes('spreadsheet')) return '📊';
        if (contentType.includes('powerpoint') || contentType.includes('presentation')) return '📊';
        if (contentType.startsWith('video/')) return '🎥';
        if (contentType.startsWith('audio/')) return '🎵';
        return '📎';
    }
    
    formatFileSize(bytes) {
        if (!bytes) return 'Unknown size';
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        if (bytes === 0) return '0 Bytes';
        const i = Math.floor(Math.log(bytes) / Math.log(1024));
        return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i];
    }
    
    async downloadAttachment(attachment) {
        try {
            console.log('[ChatWidget] Downloading attachment:', attachment.AttachmentName, 'ID:', attachment.AttachmentId);            
            // Use ChatJS built-in downloadAttachment method
            const downloadResponse = await this.chatSession.downloadAttachment({
                attachmentId: attachment.AttachmentId
            });            
            console.log('[ChatWidget] Download response:', downloadResponse);            
            // Create blob and download
            const blob = new Blob([downloadResponse], { type: attachment.ContentType });
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = attachment.AttachmentName;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);
            
        } catch (error) {
            console.error('[ChatWidget] Failed to download attachment:', error);
            this.showAttachmentError('Failed to download attachment');
        }
    }
    
    showMultipleAttachmentsInLine(files) {
        const messagesContainer = document.getElementById('chat-messages');
        const messageDiv = document.createElement('div');
        messageDiv.className = 'message customer file-message';        
        const thumbnailsContainer = document.createElement('div');
        thumbnailsContainer.className = 'file-thumbnails-chat';        
        // Show ALL files during upload phase
        files.forEach((file, index) => {
            const thumbnailDiv = document.createElement('div');
            thumbnailDiv.className = 'file-thumbnail-chat';
            thumbnailDiv.dataset.fileIndex = index;
            this.createFileThumbnail(file, thumbnailDiv, true);
            thumbnailsContainer.appendChild(thumbnailDiv);
        });        
        messageDiv.appendChild(thumbnailsContainer);
        messagesContainer.appendChild(messageDiv);
        requestAnimationFrame(() => {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });        
        // Store reference for updating states with unique batch ID
        const batchId = Date.now() + '_' + Math.random();
        messageDiv.dataset.batchId = batchId;
        this.currentAttachmentMessage = messageDiv;
        this.attachmentBatches.set(batchId, { messageDiv, attachmentIds: [] });
    }
    
    convertToGrid() {
        if (!this.currentAttachmentMessage) return;        
        const thumbnailsContainer = this.currentAttachmentMessage.querySelector('.file-thumbnails-chat');
        if (thumbnailsContainer) {
            const allThumbnails = thumbnailsContainer.querySelectorAll('.file-thumbnail-chat');
            const maxPreview = 4;            
            // Hide thumbnails beyond the 4th
            allThumbnails.forEach((thumbnail, index) => {
                if (index >= maxPreview) {
                    thumbnail.style.display = 'none';
                }
            });            
            // Add +X overlay to 4th thumbnail if there are more files
            if (allThumbnails.length > maxPreview) {
                const fourthThumbnail = allThumbnails[maxPreview - 1];
                const remainingCount = allThumbnails.length - maxPreview;
                const overlay = document.createElement('div');
                overlay.style.cssText = 'position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; color: #ffffff; font-family: "SF Pro", -apple-system, BlinkMacSystemFont, sans-serif; font-weight: 600; font-size: 18px;';
                overlay.textContent = `+${remainingCount}`;
                fourthThumbnail.style.position = 'relative';
                fourthThumbnail.appendChild(overlay);
            }            
            thumbnailsContainer.className = 'file-thumbnails-grid';
        }
        
        // Update message container width for grid
        this.currentAttachmentMessage.style.width = '200px';
        this.currentAttachmentMessage.style.maxWidth = '200px';
    }
    
    updateThumbnailState(fileIndex, state, messageDiv = null) {
        const targetMessage = messageDiv || this.currentAttachmentMessage;
        if (!targetMessage) return;        
        const thumbnail = targetMessage.querySelector(`[data-file-index="${fileIndex}"]`);
        if (!thumbnail) return;        
        const overlay = thumbnail.querySelector('.file-loading-overlay');
        if (!overlay) return;        
        if (state === 'success') {
            overlay.style.display = 'none';
        } else if (state === 'failed') {
            overlay.innerHTML = '<div class="file-failed-indicator">✗</div>';
            overlay.style.background = 'rgba(220, 53, 69, 0.8)';
        } else if (state === 'size-error' || state === 'format-error' || state === 'malware-error') {
            overlay.style.display = 'none';
            thumbnail.classList.add('has-error');
            thumbnail.style.marginLeft = '-25px';
            const errorIcon = document.createElement('div');
            errorIcon.style.cssText = 'position: absolute; right: -30px; top: 50%; transform: translateY(-50%); width: 20px; height: 20px; background: #002677; clip-path: polygon(50% 0%, 0% 100%, 100% 100%);';
            const exclamation = document.createElement('div');
            exclamation.style.cssText = 'position: absolute; left: 50%; top: 50%; transform: translate(-50%, -50%); color: white; font-size: 12px; font-weight: 900; line-height: 1; text-shadow: 0 0 1px rgba(0,0,0,0.5);';
            exclamation.textContent = '!';
            errorIcon.appendChild(exclamation);
            thumbnail.style.position = 'relative';
            thumbnail.appendChild(errorIcon);
        }
    }
    
    showTypingIndicator() {
        const indicator = document.getElementById('typing-indicator');
        indicator.style.display = 'flex';        
        // Hide after 3 seconds of no typing
        clearTimeout(this.typingTimeout);
        this.typingTimeout = setTimeout(() => {
            indicator.style.display = 'none';
        }, 3000);
    }
    
    addReadStatus(messageElement) {
        const statusDiv = document.createElement('div');
        statusDiv.className = 'message-status';
        statusDiv.innerHTML = '<span>✓ Sent</span>';        
        // Insert status after the message element, not inside it
        messageElement.parentNode.insertBefore(statusDiv, messageElement.nextSibling);        
        // Store reference for read status update
        messageElement.statusDiv = statusDiv;
    }
    
    markMessageAsRead(messageElement) {
        if (messageElement.statusDiv) {
            messageElement.statusDiv.innerHTML = '<span>✓✓ Read</span>';
        }
    }
    
    markAllCustomerMessagesAsRead() {
        const customerMessages = document.querySelectorAll('.message.customer');
        customerMessages.forEach(message => {
            if (message.statusDiv && message.statusDiv.textContent.includes('Sent')) {
                this.markMessageAsRead(message);
            }
        });
    }
    updateStatus(status) {
        document.getElementById('status').textContent = status;
    }    
    showChatClosedNotification() {
        const messagesContainer = document.getElementById('chat-messages');
        const notificationDiv = document.createElement('div');
        notificationDiv.className = 'chat-notification';
        notificationDiv.innerHTML = '🔚 Agent has ended the conversation';
        messagesContainer.appendChild(notificationDiv);
        requestAnimationFrame(() => {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });
    }    
    showAgentJoinedMessage() {
        const messagesContainer = document.getElementById('chat-messages');
        const notificationDiv = document.createElement('div');
        notificationDiv.className = 'agent-joined-notification';
        notificationDiv.style.cssText = 'font-family: "Enterprise Sans VF", sans-serif; font-weight: 500; font-size: 14px; color: #323334;';
        notificationDiv.innerHTML = `${this.agentName} has joined the chat`;
        messagesContainer.appendChild(notificationDiv);
        requestAnimationFrame(() => {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });
    }    
    showSupervisorJoinedMessage() {
        const messagesContainer = document.getElementById('chat-messages');
        const notificationDiv = document.createElement('div');
        notificationDiv.className = 'supervisor-notification';
        notificationDiv.innerHTML = `👨‍💼 ${this.supervisorName} has joined the conversation`;
        messagesContainer.appendChild(notificationDiv);
        requestAnimationFrame(() => {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });
    }    
    showSupervisorLeftMessage() {
        const messagesContainer = document.getElementById('chat-messages');
        const notificationDiv = document.createElement('div');
        notificationDiv.className = 'supervisor-notification';
        notificationDiv.innerHTML = `👨‍💼 ${this.supervisorName} has left the conversation`;
        messagesContainer.appendChild(notificationDiv);
        requestAnimationFrame(() => {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });
    }
    
    showAutoDisconnectionMessage() {
        const messagesContainer = document.getElementById('chat-messages');        
        // Agent exited message
        const agentExitDiv = document.createElement('div');
        agentExitDiv.className = 'agent-joined-notification';
        agentExitDiv.style.cssText = 'font-family: "Enterprise Sans VF", sans-serif; font-weight: 500; font-size: 14px; color: #323334; text-align: center;';
        agentExitDiv.textContent = `${this.agentName} has exited the chat`;
        messagesContainer.appendChild(agentExitDiv);        
        // Horizontal line
        const lineDiv = document.createElement('div');
        lineDiv.style.cssText = 'border-top: 1px solid #ccc; margin: 10px 0;';
        messagesContainer.appendChild(lineDiv);        
        // Connecting message with spinner
        const connectingDiv = document.createElement('div');
        connectingDiv.style.cssText = 'display: flex; align-items: center; justify-content: center; gap: 8px; font-family: "Enterprise Sans VF", sans-serif; font-weight: normal; font-size: 14px; color: #323334; margin: 5px 0;';
        
        const spinner = document.createElement('div');
        spinner.style.cssText = 'width: 16px; height: 16px; border: 2px solid #ccc; border-top: 2px solid #666; border-radius: 50%; animation: spin 1s linear infinite;';        
        const connectingText = document.createElement('span');
        connectingText.textContent = 'Connecting you to an advocate';        
        connectingDiv.appendChild(spinner);
        connectingDiv.appendChild(connectingText);
        messagesContainer.appendChild(connectingDiv);        
        // Add spinner animation CSS if not exists
        if (!document.getElementById('spinner-style')) {
            const style = document.createElement('style');
            style.id = 'spinner-style';
            style.textContent = '@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }';
            document.head.appendChild(style);
        }        
        // Patience message
        const patienceDiv = document.createElement('div');
        patienceDiv.style.cssText = 'font-family: "SF Pro", sans-serif; font-weight: 500; font-size: 12px; color: #323334; margin: 5px 0; text-align: center;';
        patienceDiv.innerHTML = 'You are being connected with another advocate.<br>Thanks for your patience!';
        messagesContainer.appendChild(patienceDiv);        
        requestAnimationFrame(() => {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });
    }
    
    updateQueueMessage(newQueueText) {
        const messagesContainer = document.getElementById('chat-messages');
        
        // Find existing queue container or create new one
        let queueContainer = document.getElementById('queue-message-container');
        
        if (!queueContainer) {
            // Create new queue container
            queueContainer = document.createElement('div');
            queueContainer.id = 'queue-message-container';
            
            // Add horizontal line separator
            const lineDiv = document.createElement('div');
            lineDiv.style.cssText = 'border-top: 1px solid #ccc; margin: 10px 0;';
            queueContainer.appendChild(lineDiv);
            
            // Connecting message with spinner
            const connectingDiv = document.createElement('div');
            connectingDiv.style.cssText = 'display: flex; align-items: center; justify-content: center; gap: 8px; font-family: "Enterprise Sans VF", sans-serif; font-weight: normal; font-size: 14px; color: #323334; margin: 5px 0;';
            
            const spinner = document.createElement('div');
            spinner.style.cssText = 'width: 16px; height: 16px; border: 2px solid #ccc; border-top: 2px solid #666; border-radius: 50%; animation: spin 1s linear infinite;';
            
            const connectingText = document.createElement('span');
            connectingText.style.fontWeight = 'bold';
            connectingText.textContent = 'Connecting you to an advocate';
            
            connectingDiv.appendChild(spinner);
            connectingDiv.appendChild(connectingText);
            queueContainer.appendChild(connectingDiv);
            
            // Queue details container
            const queueDetails = document.createElement('div');
            queueDetails.id = 'queue-details';
            queueDetails.style.cssText = 'font-family: "Enterprise Sans VF", sans-serif; font-weight: normal; font-size: 14px; color: #323334; text-align: center; margin: 8px 0; line-height: 1.5;';
            queueContainer.appendChild(queueDetails);
            
            messagesContainer.appendChild(queueContainer);
            
            // Add spinner animation CSS if not exists
            if (!document.getElementById('spinner-style')) {
                const style = document.createElement('style');
                style.id = 'spinner-style';
                style.textContent = '@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }';
                document.head.appendChild(style);
            }
        }
        
        // Update queue details - replace with new message
        const queueDetails = document.getElementById('queue-details');
        if (!this.currentQueuePosition) this.currentQueuePosition = '';
        if (!this.currentWaitTime) this.currentWaitTime = '';
        
        // Check message type and update accordingly
        if (/You are currently \d+ in queue/i.test(newQueueText)) {
            this.currentQueuePosition = newQueueText;
        } else if (/Your estimated wait time is \d+ to \d+ minutes/i.test(newQueueText)) {
            this.currentWaitTime = newQueueText;
        }
        
        // Display current queue status
        const messages = [this.currentQueuePosition, this.currentWaitTime].filter(msg => msg);
        queueDetails.innerHTML = messages.join('<br>');
        
        // Save queue state
        this.saveQueueState();
        
        requestAnimationFrame(() => {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });
    }
    
    clearAutoDisconnectionMessages() {
        console.log('[ChatWidget] === CLEARING AUTO-DISCONNECTION MESSAGES ===');
        const messagesContainer = document.getElementById('chat-messages');
        const allElements = messagesContainer.children;
        console.log('[ChatWidget] Total elements in chat:', allElements.length);        
        const elementsToRemove = [];
        for (let element of allElements) {
            const text = element.textContent || '';
            const hasConnectingText = text.includes('Connecting you to an advocate');
            const hasBeingConnectedText = text.includes('You are being connected');            
            console.log('[ChatWidget] Checking element:', text.substring(0, 50), {
                hasConnectingText, hasBeingConnectedText
            });            
            if (hasConnectingText || hasBeingConnectedText) {
                console.log('[ChatWidget] Marking for removal:', text.substring(0, 50));
                elementsToRemove.push(element);
            }
        }        
        console.log('[ChatWidget] Elements to remove:', elementsToRemove.length);
        elementsToRemove.forEach(element => {
            console.log('[ChatWidget] Removing:', element.textContent.substring(0, 50));
            element.remove();
        });
        console.log('[ChatWidget] === CLEAR COMPLETE ===');
    }
    
    clearQueueMessages() {
        const queueContainer = document.getElementById('queue-message-container');
        if (queueContainer) {
            queueContainer.remove();
        }
        this.currentQueuePosition = '';
        this.currentWaitTime = '';
        localStorage.removeItem('queueState');
    }
    
    saveQueueState() {
        if (this.currentQueuePosition || this.currentWaitTime) {
            localStorage.setItem('queueState', JSON.stringify({
                position: this.currentQueuePosition,
                waitTime: this.currentWaitTime
            }));
        }
    }
    
    restoreQueueState() {
        const queueState = localStorage.getItem('queueState');
        if (queueState && !this.agentConnected) {
            const { position, waitTime } = JSON.parse(queueState);
            this.currentQueuePosition = position || '';
            this.currentWaitTime = waitTime || '';
            
            if (this.currentQueuePosition || this.currentWaitTime) {
                // Recreate queue display
                const messagesContainer = document.getElementById('chat-messages');
                if (messagesContainer && !document.getElementById('queue-message-container')) {
                    this.updateQueueMessage(this.currentQueuePosition || this.currentWaitTime);
                }
            }
        }
    }
    
    async handleAutoDisconnection() {
        console.log('[ChatWidget] Handling auto-disconnection for contact ID:', this.contactId);
        
        if (this.contactId) {
            try {
                await fetch(CONFIG.disconnectChatUrl, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ contactId: this.contactId })
                });
                console.log('[ChatWidget] Auto-disconnection notification sent successfully');
            } catch (error) {
                console.error('[ChatWidget] Failed to send auto-disconnection notification:', error);
            }
        }
    }
    
    showIdleNotification(participant, isIdle) {
        if (this.surveyMode) return;
        const messagesContainer = document.getElementById('chat-messages');
        const notificationDiv = document.createElement('div');
        notificationDiv.className = 'chat-notification';
        const participantName = participant === 'AGENT' ? 'Agent' : 'Customer';
        const status = isIdle ? 'went idle' : 'returned';
        notificationDiv.innerHTML = `⏸️ ${participantName} ${status}`;
        messagesContainer.appendChild(notificationDiv);
        requestAnimationFrame(() => {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });
    }
    
    async loadTranscriptLazily() {
        try {
            // Load only 10 recent messages initially
            const response = await this.chatSession.getTranscript({
                maxResults: 10
            });
            
            this.storedTranscript = response.data.Transcript || [];
            this.nextToken = response.data.NextToken;
            
            if (this.storedTranscript.length > 0) {
                // Sort messages by timestamp to ensure correct order
                this.storedTranscript.sort((a, b) => new Date(a.AbsoluteTime) - new Date(b.AbsoluteTime));
                this.displayStoredTranscript();
                this.setupScrollListener();
            }
        } catch (error) {
            console.error('[ChatWidget] Error loading transcript:', error);
        }
    }
    
    setupScrollListener() {
        const messagesContainer = document.getElementById('chat-messages');
        messagesContainer.addEventListener('scroll', async () => {
            // Check if scrolled near the top (within 50px)
            if (messagesContainer.scrollTop <= 50 && this.nextToken && !this.loadingMoreMessages) {
                this.loadingMoreMessages = true;
                const currentScrollHeight = messagesContainer.scrollHeight;
                await this.loadMoreMessages();
                // Maintain scroll position after loading
                const newScrollHeight = messagesContainer.scrollHeight;
                messagesContainer.scrollTop = newScrollHeight - currentScrollHeight;
                this.loadingMoreMessages = false;
            }
        });
    }
    
    async loadMoreMessages() {
        try {
            const response = await this.chatSession.getTranscript({
                nextToken: this.nextToken,
                maxResults: 10
            });
            
            const moreMessages = response.data.Transcript || [];
            if (moreMessages.length > 0) {
                // Sort new messages by timestamp
                moreMessages.sort((a, b) => new Date(a.AbsoluteTime) - new Date(b.AbsoluteTime));
                // Update stored transcript and token
                this.storedTranscript = [...moreMessages, ...(this.storedTranscript || [])];
                this.nextToken = response.data.NextToken;
                this.prependMessages(moreMessages);
            } else {
                // No more messages available
                this.nextToken = null;
            }
        } catch (error) {
            console.error('[ChatWidget] Error loading more messages:', error);
            this.nextToken = null; // Stop trying on error
        }
    }
    
    prependMessages(messages) {
        const messagesContainer = document.getElementById('chat-messages');
        const fragment = document.createDocumentFragment();
        
        // Sort messages chronologically before prepending
        const sortedMessages = [...messages].sort((a, b) => 
            new Date(a.AbsoluteTime) - new Date(b.AbsoluteTime)
        );
        
        // Group messages by date
        const messagesByDate = {};
        sortedMessages.forEach(item => {
            if (item.Type === 'MESSAGE' && item.Content) {
                const messageDate = new Date(item.AbsoluteTime);
                const dateString = messageDate.toDateString();
                if (!messagesByDate[dateString]) {
                    messagesByDate[dateString] = [];
                }
                messagesByDate[dateString].push(item);
            }
        });
        
        // Process each date group
        Object.keys(messagesByDate).forEach(dateString => {
            // Check if this date already has a separator in the container
            const existingSeparator = Array.from(messagesContainer.querySelectorAll('.date-separator'))
                .find(sep => sep.textContent === dateString || (dateString === new Date().toDateString() && sep.textContent === 'Today'));
            
            if (!existingSeparator) {
                // Add date separator only if it doesn't exist
                const dateSeparator = document.createElement('div');
                dateSeparator.className = 'date-separator';
                const today = new Date().toDateString();
                dateSeparator.textContent = dateString === today ? 'Today' : dateString;
                dateSeparator.style.cssText = 'text-align: center; color: #666; font-size: 12px; font-weight: bold; margin: 15px 0 10px 0; padding: 5px;';
                fragment.appendChild(dateSeparator);
            }
            
            // Add all messages for this date
            messagesByDate[dateString].forEach(item => {
                const messageId = `${item.Id || item.AbsoluteTime}_${item.ParticipantRole}_${item.Content.substring(0, 50)}`;
                if (!this.displayedMessageIds.has(messageId)) {
                    this.displayedMessageIds.add(messageId);
                    this.createTranscriptMessage(item, fragment);
                }
            });
        });
        
        // Insert before the first child (which should be existing transcript messages)
        const firstChild = messagesContainer.firstChild;
        messagesContainer.insertBefore(fragment, firstChild);
    }
    
    async getPastTranscript(nextToken, currentTranscript) {
        try {
            const response = await this.chatSession.getTranscript({
                nextToken: nextToken,
                scanDirection: 'BACKWARD',
                maxResults: 100
            });
            // Prepend past messages to maintain chronological order
            const updatedTranscript = [...(response.data.Transcript || []), ...currentTranscript];
            // Continue if more past messages exist
            if (response.data.NextToken) {
                return this.getPastTranscript(response.data.NextToken, updatedTranscript);
            }
            return updatedTranscript;
        } catch (error) {
            console.error('[ChatWidget] Error getting past transcript:', error);
            return currentTranscript;
        }
    }
    
    /**
     * Convert JSON structured content to readable text
     * @param {string} content - Raw message content that might contain JSON
     * @returns {string} - Converted text content
     */
    convertJsonToText(content) {
        try {
            // Decode HTML entities first
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = content;
            const decodedContent = tempDiv.textContent || tempDiv.innerText || content;
            // Try to parse as JSON
            const parsed = JSON.parse(decodedContent);
            // If it's structured content, extract the readable text
            if (parsed.templateType && parsed.data && parsed.data.content) {
                let text = parsed.data.content.title || '';
                // Add options as text if they exist
                if (parsed.data.content.elements && parsed.data.content.elements.length > 0) {
                    const options = parsed.data.content.elements.map(el => el.title).join(', ');
                    text += `\n\nOptions: ${options}`;
                }
                return text;
            }
        } catch (e) {
            // Not JSON or not structured content, return original
        }
        
        return content;
    }

    async captureInfo() {
        // Extract device platform info from user agent
        const ua = navigator.userAgent;
        let deviceName = 'Unknown';
        let devicePlatform = 'Unknown';
        
        if (/iPhone|iPad|iPod/.test(ua)) {
            devicePlatform = 'Mobile';
            const match = ua.match(/OS ([\d_]+)/);
            deviceName = match ? `iOS ${match[1].replace(/_/g, '.')}` : 'iOS';
        } else if (/Android/.test(ua)) {
            devicePlatform = 'Mobile';
            const match = ua.match(/Android ([\d.]+)/);
            deviceName = match ? `Android ${match[1]}` : 'Android';
        } else if (/Windows/.test(ua)) {
            devicePlatform = 'Desktop';
            deviceName = 'Windows';
        } else if (/Mac/.test(ua)) {
            devicePlatform = 'Desktop';
            deviceName = 'macOS';
        } else if (/Linux/.test(ua)) {
            devicePlatform = 'Desktop';
            deviceName = 'Linux';
        }
        
        const deviceInfo = {
            userAgent: navigator.userAgent,
            platform: navigator.platform,
            language: navigator.language,
            deviceName: deviceName,
            devicePlatform: devicePlatform,
            screen: {
                width: screen.width,
                height: screen.height
            }
        };

        const stateMap = {
            'Alabama': 'AL', 'Alaska': 'AK', 'Arizona': 'AZ', 'Arkansas': 'AR', 'California': 'CA',
            'Colorado': 'CO', 'Connecticut': 'CT', 'Delaware': 'DE', 'Florida': 'FL', 'Georgia': 'GA',
            'Hawaii': 'HI', 'Idaho': 'ID', 'Illinois': 'IL', 'Indiana': 'IN', 'Iowa': 'IA',
            'Kansas': 'KS', 'Kentucky': 'KY', 'Louisiana': 'LA', 'Maine': 'ME', 'Maryland': 'MD',
            'Massachusetts': 'MA', 'Michigan': 'MI', 'Minnesota': 'MN', 'Mississippi': 'MS', 'Missouri': 'MO',
            'Montana': 'MT', 'Nebraska': 'NE', 'Nevada': 'NV', 'New Hampshire': 'NH', 'New Jersey': 'NJ',
            'New Mexico': 'NM', 'New York': 'NY', 'North Carolina': 'NC', 'North Dakota': 'ND', 'Ohio': 'OH',
            'Oklahoma': 'OK', 'Oregon': 'OR', 'Pennsylvania': 'PA', 'Rhode Island': 'RI', 'South Carolina': 'SC',
            'South Dakota': 'SD', 'Tennessee': 'TN', 'Texas': 'TX', 'Utah': 'UT', 'Vermont': 'VT',
            'Virginia': 'VA', 'Washington': 'WA', 'West Virginia': 'WV', 'Wisconsin': 'WI', 'Wyoming': 'WY'
        };

        let locationInfo = {
            city: 'Unknown',
            region: 'Unknown',
            country: 'US',
            ip: 'Unknown',
            ContactStateCD: 'Unknown'
        };
        
        // Try browser geolocation API (no CSP issues)
        if (navigator.geolocation) {
            try {
                const position = await new Promise((resolve, reject) => {
                    navigator.geolocation.getCurrentPosition(resolve, reject, {
                        timeout: 5000,
                        enableHighAccuracy: false
                    });
                });
                locationInfo.latitude = position.coords.latitude;
                locationInfo.longitude = position.coords.longitude;
                console.log('[ChatWidget] Browser location obtained');
            } catch (e) {
                console.log('[ChatWidget] Browser location not available');
            }
        }

        const payload = { deviceInfo, locationInfo };
        console.log('[ChatWidget] Collected Info:', payload);
        return payload;
    }

    displayStoredTranscript() {
        if (!this.storedTranscript || this.storedTranscript.length === 0) return;
        const messagesContainer = document.getElementById('chat-messages');
        
        // Clear all existing messages to rebuild conversation properly
        messagesContainer.innerHTML = '';
        this.displayedMessageIds.clear();
        
        // Sort messages chronologically before displaying
        const sortedMessages = [...this.storedTranscript].sort((a, b) => 
            new Date(a.AbsoluteTime) - new Date(b.AbsoluteTime)
        );
        
        const transcriptFragment = document.createDocumentFragment();
        let agentJoinedShown = false;
        
        // Add "Today" separator at the beginning
        const today = new Date().toDateString();
        const dateSeparator = document.createElement('div');
        dateSeparator.className = 'date-separator';
        dateSeparator.textContent = 'Today';
        dateSeparator.style.cssText = 'text-align: center; color: #666; font-size: 12px; font-weight: bold; margin: 15px 0 10px 0; padding: 5px;';
        transcriptFragment.appendChild(dateSeparator);
        
        sortedMessages.forEach(item => {
            // Show agent joined notification at exact position based on EVENT
            if (!agentJoinedShown && item.Type === 'EVENT' && 
                item.ContentType === 'application/vnd.amazonaws.connect.event.participant.joined' &&
                item.ParticipantRole === 'AGENT' && this.agentConnected) {
                const notificationDiv = document.createElement('div');
                notificationDiv.className = 'agent-joined-notification';
                notificationDiv.style.cssText = 'font-family: "Enterprise Sans VF", sans-serif; font-weight: 500; font-size: 14px; color: #323334;';
                notificationDiv.innerHTML = `${this.agentName} has joined the chat`;
                transcriptFragment.appendChild(notificationDiv);
                agentJoinedShown = true;
            }
            
            if (item.Type === 'MESSAGE' && item.Content) {
                const messageId = `${item.Id || item.AbsoluteTime}_${item.ParticipantRole}_${item.Content}`;
                if (!this.displayedMessageIds.has(messageId)) {
                    this.displayedMessageIds.add(messageId);
                    this.createTranscriptMessage(item, transcriptFragment);
                }
            }
        });
        
        messagesContainer.appendChild(transcriptFragment);
        requestAnimationFrame(() => {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });
    }
    
    prependMessages(messages) {
        const messagesContainer = document.getElementById('chat-messages');
        const fragment = document.createDocumentFragment();
        
        // Sort messages chronologically before prepending
        const sortedMessages = [...messages].sort((a, b) => 
            new Date(a.AbsoluteTime) - new Date(b.AbsoluteTime)
        );
        
        // Group messages by date
        const messagesByDate = {};
        sortedMessages.forEach(item => {
            if (item.Type === 'MESSAGE' && item.Content) {
                const messageDate = new Date(item.AbsoluteTime);
                const dateString = messageDate.toDateString();
                if (!messagesByDate[dateString]) {
                    messagesByDate[dateString] = [];
                }
                messagesByDate[dateString].push(item);
            }
        });
        
        // Process each date group
        Object.keys(messagesByDate).forEach(dateString => {
            // Check if this date already has a separator in the container
            const existingSeparator = Array.from(messagesContainer.querySelectorAll('.date-separator'))
                .find(sep => sep.textContent === dateString || (dateString === new Date().toDateString() && sep.textContent === 'Today'));
            
            if (!existingSeparator) {
                // Add date separator only if it doesn't exist
                const dateSeparator = document.createElement('div');
                dateSeparator.className = 'date-separator';
                const today = new Date().toDateString();
                dateSeparator.textContent = dateString === today ? 'Today' : dateString;
                dateSeparator.style.cssText = 'text-align: center; color: #666; font-size: 12px; font-weight: bold; margin: 15px 0 10px 0; padding: 5px;';
                fragment.appendChild(dateSeparator);
            }
            
            // Add all messages for this date
            messagesByDate[dateString].forEach(item => {
                const messageId = `${item.Id || item.AbsoluteTime}_${item.ParticipantRole}_${item.Content.substring(0, 50)}`;
                if (!this.displayedMessageIds.has(messageId)) {
                    this.displayedMessageIds.add(messageId);
                    this.createTranscriptMessage(item, fragment);
                }
            });
        });
        
        // Insert before the first child (which should be existing transcript messages)
        const firstChild = messagesContainer.firstChild;
        messagesContainer.insertBefore(fragment, firstChild);
    }
    
    createTranscriptMessage(item, container) {
        const messageDate = new Date(item.AbsoluteTime);
        const formattedDateTime = this.formatTranscriptDateTime(messageDate);
        
        if (item.ParticipantRole === 'CUSTOMER') {
            const messageWrapper = document.createElement('div');
            messageWrapper.className = 'message-wrapper customer-wrapper';
            
            const customerInfo = document.createElement('div');
            customerInfo.className = 'customer-info';
            customerInfo.textContent = formattedDateTime;
            
            const messageDiv = document.createElement('div');
            messageDiv.className = 'message customer transcript-message';
            messageDiv.style.opacity = '0.7';
            messageDiv.textContent = this.convertJsonToText(item.Content);
            
            const messageContent = document.createElement('div');
            messageContent.className = 'message-content';
            messageContent.appendChild(customerInfo);
            messageContent.appendChild(messageDiv);
            
            messageWrapper.appendChild(messageContent);
            container.appendChild(messageWrapper);
        } else if (item.ParticipantRole === 'AGENT') {
            const messageWrapper = document.createElement('div');
            messageWrapper.className = 'message-wrapper agent-wrapper';
            
            const agentIcon = document.createElement('div');
            agentIcon.className = 'agent-icon';
            const agentName = item.DisplayName || this.agentName || 'Agent';
            agentIcon.textContent = agentName.charAt(0).toUpperCase();
            
            const agentInfo = document.createElement('div');
            agentInfo.className = 'agent-info';
            agentInfo.textContent = `${agentName} ${formattedDateTime}`;
            
            const messageDiv = document.createElement('div');
            messageDiv.className = 'message agent transcript-message';
            messageDiv.style.opacity = '0.7';
            messageDiv.innerHTML = this.formatMessageContent(this.convertJsonToText(item.Content));
            
            const messageContent = document.createElement('div');
            messageContent.className = 'message-content';
            messageContent.appendChild(agentInfo);
            messageContent.appendChild(messageDiv);
            
            messageWrapper.appendChild(agentIcon);
            messageWrapper.appendChild(messageContent);
            container.appendChild(messageWrapper);
        } else {
            const messageWrapper = document.createElement('div');
            messageWrapper.className = 'message-wrapper';
            
            const botIcon = document.createElement('div');
            botIcon.className = 'bot-icon';
            botIcon.textContent = '🤖';
            
            const messageDiv = document.createElement('div');
            messageDiv.className = 'message system transcript-message';
            messageDiv.style.opacity = '0.7';
            messageDiv.innerHTML = this.formatMessageContent(this.convertJsonToText(item.Content));
            
            messageWrapper.appendChild(botIcon);
            messageWrapper.appendChild(messageDiv);
            container.appendChild(messageWrapper);
        }
    }
    
    formatTranscriptDateTime(date) {
        const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
        const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
        
        const dayName = days[date.getDay()];
        const monthName = months[date.getMonth()];
        const dayNum = date.getDate();
        const hours = date.getHours();
        const minutes = date.getMinutes().toString().padStart(2, '0');
        const ampm = hours >= 12 ? 'PM' : 'AM';
        const displayHours = hours % 12 || 12;
        
        return `${dayName}, ${monthName} ${dayNum}, ${displayHours}:${minutes} ${ampm}`;
    }
    
    rebuildMessagesWithDateGrouping() {
        const messagesContainer = document.getElementById('chat-messages');
        
        // Extract all messages with their timestamps
        const allMessages = [];
        const messageElements = messagesContainer.querySelectorAll('.message');
        
        messageElements.forEach(msgEl => {
            const timestamp = new Date(); // Use current time for real-time messages
            const dateString = timestamp.toDateString();
            allMessages.push({ element: msgEl, dateString, timestamp });
        });
        
        // Group by date
        const messagesByDate = {};
        allMessages.forEach(msg => {
            if (!messagesByDate[msg.dateString]) {
                messagesByDate[msg.dateString] = [];
            }
            messagesByDate[msg.dateString].push(msg.element);
        });
        
        // Clear and rebuild with date separators
        messagesContainer.innerHTML = '';
        
        Object.keys(messagesByDate).sort((a, b) => new Date(a) - new Date(b)).forEach(dateString => {
            // Add date separator
            const dateSeparator = document.createElement('div');
            dateSeparator.className = 'date-separator';
            const today = new Date().toDateString();
            dateSeparator.textContent = dateString === today ? 'Today' : dateString;
            dateSeparator.style.cssText = 'text-align: center; color: #666; font-size: 12px; font-weight: bold; margin: 15px 0 10px 0; padding: 5px;';
            messagesContainer.appendChild(dateSeparator);
            
            // Add all messages for this date
            messagesByDate[dateString].forEach(msgEl => {
                const wrapper = msgEl.closest('.message-wrapper');
                if (wrapper) {
                    messagesContainer.appendChild(wrapper);
                } else {
                    messagesContainer.appendChild(msgEl);
                }
            });
        });
    }

    /**
     * Send attachment IDs to scan URL for bulk processing
     */
    async scanAttachments(batchId) {
        const batch = this.attachmentBatches.get(batchId);
        if (!batch || batch.attachmentIds.length === 0) return;
        
        try {
            console.log('[ChatWidget] Sending attachment IDs for scanning:', batch.attachmentIds);
            
            const response = await fetch(CONFIG.scanAttachmentStatusUrl, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    AttachmentIds: batch.attachmentIds
                })
            });
            
            if (response.ok) {
                const scanResults = await response.json();
                console.log('[ChatWidget] Attachment scan results:', scanResults);
                this.processAttachmentScanResults(scanResults, batchId);
            } else {
                console.error('[ChatWidget] Failed to send attachment scan request:', response.status);
            }
        } catch (error) {
            console.error('[ChatWidget] Error sending attachment scan request:', error);
        }
    }

    /**
     * Process attachment scan results and update UI accordingly
     */
    processAttachmentScanResults(scanResults, batchId) {
        const batch = this.attachmentBatches.get(batchId);
        if (!batch || !Array.isArray(scanResults)) return;
        
        let hasAcceptedAttachment = false;
        scanResults.forEach((result, index) => {
            const attachmentId = result.attachmentId;
            const status = result.status;
            
            // Find the corresponding thumbnail by attachment ID index
            const attachmentIndex = batch.attachmentIds.indexOf(attachmentId);
            if (attachmentIndex !== -1) {
                if (status === 'REJECTED') {
                    this.updateThumbnailState(attachmentIndex, 'malware-error', batch.messageDiv);
                    this.showFileError(attachmentIndex, 'Potential Malware', batch.messageDiv);
                } else {
                    // For any non-rejected status, hide loading overlay and count as sent
                    this.updateThumbnailState(attachmentIndex, 'success', batch.messageDiv);
                    this.uploadCount++;
                    hasAcceptedAttachment = true;
                }
            }
        });
        
        // Add Sent status only if at least one attachment was accepted
        if (hasAcceptedAttachment) {
            this.addReadStatus(batch.messageDiv);
        }
        
        // After processing all scan results, show remaining uploads and convert to grid if needed
        this.showRemainingUploads();
        
        const allApproved = scanResults.every(result => result.status !== 'REJECTED');
        if (scanResults.length > 1 && allApproved) {
            this.convertToGrid();
        }
        
        // Clear attachment IDs array after processing scan results
        this.attachmentIds = [];
    }

    /**
     * Clear chat attachments from input box
     */
    clearChatAttachments() {
        // Clear selected files array
        this.selectedFiles = [];
        
        // Clear file thumbnails from input
        const thumbnailsContainer = document.getElementById('file-thumbnails');
        if (thumbnailsContainer) {
            thumbnailsContainer.innerHTML = '';
        }
        
        // Clear file input value
        const fileInput = document.getElementById('file-input');
        if (fileInput) {
            fileInput.value = '';
        }
        
        // Remove has-attachments class
        const inputWrapper = document.querySelector('.input-wrapper');
        if (inputWrapper) {
            inputWrapper.classList.remove('has-attachments');
        }
    }

    async printTranscript() {
        try {
            console.log('[ChatWidget] ===== STARTING PRINT TRANSCRIPT =====');
            
            // Load complete transcript first
            const completeTranscript = await this.getCompleteTranscript();
            
            console.log(`[ChatWidget] Complete transcript loaded: ${completeTranscript.length} total messages`);
            
            // Filter and sort messages that will be included in PDF
            const pdfMessages = completeTranscript
                .filter(item => item.Type === 'MESSAGE' && item.Content)
                .sort((a, b) => new Date(a.AbsoluteTime) - new Date(b.AbsoluteTime));
            console.log(`[ChatWidget] Messages to include in PDF: ${pdfMessages.length}`);
            
            // Show breakdown of what's being filtered out
            const filteredOut = completeTranscript.filter(item => !(item.Type === 'MESSAGE' && item.Content));
            console.log(`[ChatWidget] Messages filtered out: ${filteredOut.length}`);
            
            if (filteredOut.length > 0) {
                const filteredTypes = {};
                filteredOut.forEach(msg => {
                    const key = `${msg.Type}-${msg.ParticipantRole || 'UNKNOWN'}`;
                    filteredTypes[key] = (filteredTypes[key] || 0) + 1;
                });
                console.log('[ChatWidget] Filtered out message types:', filteredTypes);
            }
            
            let printContent = '<div class="print-content"><h1>Chat Transcript</h1>';
            printContent += '<div style="margin-bottom: 20px; font-size: 14px; color: #666;">Generated on: ' + new Date().toLocaleString() + '</div>';
            printContent += `<div style="margin-bottom: 20px; font-size: 12px; color: #999;">Total messages retrieved: ${completeTranscript.length} | Messages in PDF: ${pdfMessages.length}</div>`;
            
            if (pdfMessages.length === 0) {
                printContent += '<div style="margin: 20px 0; text-align: center; color: #666;">No chat messages found to display.</div>';
                if (completeTranscript.length > 0) {
                    printContent += '<div style="margin: 20px 0; text-align: center; color: #999; font-size: 12px;">Note: ' + completeTranscript.length + ' total items were retrieved but contained no displayable messages.</div>';
                }
            } else {
                let messageCount = 0;
                const printedMessageIds = new Set();
                
                // Process all transcript messages
                pdfMessages.forEach(item => {
                    const messageId = `${item.Id || item.AbsoluteTime}_${item.ParticipantRole}_${item.Content}`;
                    
                    if (!printedMessageIds.has(messageId)) {
                        printedMessageIds.add(messageId);
                        
                        const content = this.convertJsonToText(item.Content);
                        const messageDate = new Date(item.AbsoluteTime);
                        const formattedDateTime = this.formatTranscriptDateTime(messageDate);
                        
                        messageCount++;
                        
                        if (item.ParticipantRole === 'CUSTOMER') {
                            printContent += `<div style="margin: 10px 0; padding: 8px; background: #e3f2fd; border-radius: 8px; text-align: right;"><strong>You (${formattedDateTime}):</strong> ${content}</div>`;
                        } else if (item.ParticipantRole === 'AGENT') {
                            const agentName = item.DisplayName || this.agentName || 'Agent';
                            printContent += `<div style="margin: 10px 0; padding: 8px; background: #f5f5f5; border-radius: 8px;"><strong>${agentName} (${formattedDateTime}):</strong> ${content}</div>`;
                        } else {
                            printContent += `<div style="margin: 10px 0; padding: 8px; background: #fff3e0; border-radius: 8px;"><strong>System (${formattedDateTime}):</strong> ${content}</div>`;
                        }
                    }
                });
                
                console.log(`[ChatWidget] Actually processed ${messageCount} messages for PDF`);
                
                printContent += `<div style="margin: 20px 0; text-align: center; font-weight: bold; color: #666; border-top: 2px solid #333; padding-top: 10px;">--- End of Conversation ---</div>`;
            }
            
            printContent += '</div>';
            
            console.log('[ChatWidget] Opening print window...');
            
            const printWindow = window.open('', '_blank');
            if (!printWindow) {
                alert('Please allow popups to print the transcript');
                return;
            }
            
            printWindow.document.write(`<html><head><title>Chat Transcript</title><style>body{font-family:Arial,sans-serif;margin:20px;line-height:1.6;}</style></head><body>${printContent}</body></html>`);
            printWindow.document.close();
            printWindow.focus();
            printWindow.print();
            printWindow.addEventListener('afterprint', () => printWindow.close());
            
            console.log('[ChatWidget] ===== PRINT TRANSCRIPT COMPLETE =====');
            
        } catch (error) {
            console.error('[ChatWidget] Error printing complete transcript:', error);
            alert('Error loading complete transcript for printing');
        }
    }
    
    async getCompleteTranscript() {
        if (!this.chatSession) return [];
        
        try {
            let allMessages = [];
            let nextToken = null;
            let requestCount = 0;
            
            console.log('[ChatWidget] ===== STARTING COMPLETE TRANSCRIPT RETRIEVAL =====');
            
            // Get all messages using backward scan to retrieve complete history
            do {
                const params = { 
                    maxResults: 100,
                    scanDirection: 'BACKWARD'
                };
                if (nextToken) params.nextToken = nextToken;
                
                console.log(`[ChatWidget] Fetching BACKWARD transcript page ${requestCount + 1}...`);
                const response = await this.chatSession.getTranscript(params);
                const messages = response.data.Transcript || [];
                
                console.log(`[ChatWidget] Retrieved ${messages.length} messages on BACKWARD page ${requestCount + 1}`);
                
                if (messages.length > 0) {
                    // Prepend messages to maintain chronological order
                    allMessages = [...messages, ...allMessages];
                }
                
                nextToken = response.data.NextToken;
                requestCount++;
                
                // Add delay between requests
                if (nextToken) {
                    await new Promise(resolve => setTimeout(resolve, 300));
                }
            } while (nextToken && requestCount < 100); // Increased safety limit
            
            console.log(`[ChatWidget] ===== TRANSCRIPT RETRIEVAL COMPLETE =====`);
            console.log(`[ChatWidget] Total messages retrieved: ${allMessages.length}`);
            console.log(`[ChatWidget] Total API requests made: ${requestCount}`);
            
            // Log message types for debugging
            const messageTypes = {};
            allMessages.forEach(msg => {
                const key = `${msg.Type}-${msg.ParticipantRole}`;
                messageTypes[key] = (messageTypes[key] || 0) + 1;
            });
            console.log('[ChatWidget] Message breakdown by type:', messageTypes);
            
            // Count messages that will be included in PDF (MESSAGE type only)
            const pdfMessages = allMessages.filter(msg => msg.Type === 'MESSAGE' && msg.Content);
            console.log(`[ChatWidget] Messages that will be included in PDF: ${pdfMessages.length}`);
            console.log(`[ChatWidget] Messages filtered out: ${allMessages.length - pdfMessages.length}`);
            
            // Sort by timestamp to ensure correct order
            allMessages.sort((a, b) => new Date(a.AbsoluteTime) - new Date(b.AbsoluteTime));
            
            // Log first and last message timestamps for verification
            if (allMessages.length > 0) {
                console.log(`[ChatWidget] First message: ${allMessages[0].AbsoluteTime} (${allMessages[0].ParticipantRole})`);
                console.log(`[ChatWidget] Last message: ${allMessages[allMessages.length - 1].AbsoluteTime} (${allMessages[allMessages.length - 1].ParticipantRole})`);
            }
            
            return allMessages;
            
        } catch (error) {
            console.error('[ChatWidget] Error getting complete transcript:', error);
            return [];
        }
    }
    


}

/**
 * Inject minimal required CSS styles
 */
function injectStyles() {
    const styles = `
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: #f8f9fa; color: #333; min-height: 100vh; display: flex; flex-direction: column; margin: 0; padding: 0; }
        .main-content { width: 100%; padding: 10px 20px; flex: 1; }
        .chat-icon { position: fixed; bottom: 20px; right: 20px; width: 80px; height: 50px; background: #003366; border-radius: 25px; display: flex; align-items: center; justify-content: center; gap: 6px; font-size: 14px; font-weight: 600; color: white; cursor: pointer; box-shadow: 0 6px 20px rgba(0, 51, 102, 0.3); z-index: 1000; transition: all 0.3s ease; border: 2px solid rgba(255, 255, 255, 0.2); }
        .chat-icon:hover { transform: translateY(-2px) scale(1.05); box-shadow: 0 8px 25px rgba(0, 51, 102, 0.4); background: #004080; }
        .ai-icon { font-size: 20px; animation: pulse 2s infinite; filter: drop-shadow(0 1px 2px rgba(0,0,0,0.2)); }
        .ai-text { font-weight: 700; letter-spacing: 0.8px; text-shadow: 0 1px 2px rgba(0,0,0,0.1); font-size: 13px; }
        .menu-lines { font-size: 12px; opacity: 0.8; }
        @keyframes pulse { 0%, 100% { transform: scale(1); } 50% { transform: scale(1.1); } }
        .chat-tooltip { position: absolute; bottom: 60px; right: 0; background: #333; color: white; padding: 8px 12px; border-radius: 8px; font-size: 12px; white-space: nowrap; opacity: 0; transform: translateY(10px); transition: all 0.3s ease; pointer-events: none; }
        .chat-tooltip.show { opacity: 1; transform: translateY(0); }
        .chat-tooltip::after { content: ''; position: absolute; top: 100%; right: 20px; border: 5px solid transparent; border-top-color: #333; }
        .chat-container { position: fixed; bottom: 20px; right: 20px; width: min(400px, calc(100vw - 40px)); height: min(calc((100vh - 24px) * 0.8), calc(100vh - 40px)); background: white; border-radius: 8px; box-shadow: 0 20px 60px rgba(0,0,0,0.15); z-index: 1001; display: flex; flex-direction: column; overflow: hidden; font-family: Arial, sans-serif; overflow-x: hidden; }
        .chat-header { color: black; padding: 16px 20px; display: flex; justify-content: center; align-items: center; position: relative; border-bottom: 1px solid #e0e0e0; }
        .menu-btn { position: absolute; left: 20px; background: transparent; border: none; color: black; font-size: 24px; font-weight: bold; cursor: pointer; padding: 0; width: 24px; height: 24px; display: flex; align-items: center; justify-content: center; border-radius: 4px; }
        .menu-btn:hover { background: rgba(0,0,0,0.1); }
        .chat-header h2 { margin: 0; font-size: 16px; font-weight: 600; }
        .header-buttons { position: absolute; top: 16px; right: 20px; display: flex; gap: 8px; }
        .header-buttons button { background: transparent; border: none; color: black; font-size: 18px; cursor: pointer; padding: 0; width: 24px; height: 24px; display: flex; align-items: center; justify-content: center; border-radius: 4px; }
        .header-buttons button:hover { background: rgba(0,0,0,0.1); }
        .chat-messages { flex: 1; padding: 16px; overflow-y: overlay; overflow-x: hidden; background: #fff; display: flex; flex-direction: column; max-height: calc(100% - 120px); min-height: 0; position: relative; height: 100%; }
        .chat-messages.has-messages { justify-content: flex-start; }
        .structured-message-wrapper.first-bot-message, .message-wrapper.first-bot-message { position: relative; margin: 0.5rem auto; padding: 0.5rem; width: 100%; max-width: 100%; text-align: left; font-size: clamp(16px, 4vw, 18px); min-height: auto; top: 5%;}
        .structured-message-wrapper.first-bot-message .message h2, .message-wrapper.first-bot-message .message h2 { color: #003366 !important; margin-bottom: 1rem; }
        .structured-message-wrapper.first-bot-message .message, .message-wrapper.first-bot-message .message { background: transparent !important; color: #003366 !important; font-family: serif !important; font-weight: bold !important; word-wrap: break-word; }
        .message-wrapper.first-bot-message .bot-icon { display: none; }
        .message-wrapper .bot-icon { display: flex; }
        .structured-message-wrapper { display: flex; flex-direction: column; gap: 4px; flex-wrap: wrap; }
        .chat-input-container { position: absolute; bottom: 0; left: 0; right: 0; padding: 16px; background: white; display: flex; gap: 8px; align-items: center; border-top: 1px solid #e0e0e0; }
        .input-wrapper { flex: 1; position: relative; border: 2px solid #e0e0e0; border-radius: 0px; background: white; min-height: 44px; display: flex; align-items: center; padding: 8px 12px; gap: 6px; overflow-x: auto; }
        .input-wrapper.has-attachments { flex-direction: column; align-items: stretch; }
        .input-wrapper:focus-within { border-color: #196ECF; }
        .file-thumbnails { display: flex; gap: 6px; flex-wrap: nowrap; flex-shrink: 0; margin-bottom: 8px; }
        .file-thumbnail { position: relative; width: 50px; height: 30px; background: #f0f0f0; border-radius: 4px; display: flex; align-items: center; justify-content: center; font-size: 10px; color: #666; font-weight: bold; }
        .file-thumbnail .remove-file { position: absolute; top: -5px; right: -5px; width: 16px; height: 16px; background: #dc3545; color: white; border: none; border-radius: 50%; font-size: 10px; cursor: pointer; display: flex; align-items: center; justify-content: center; }
        .attachment-error { background: #ffebee; border-bottom: 1px solid #ffcdd2; padding: 12px 16px; margin: 0; display: flex; align-items: center; gap: 12px; z-index: 1000; position: absolute; bottom: var(--input-height, 76px); left: 0; right: 0; width: 400px; height: 70px; }
        .error-icon { width: 20px; height: 20px; background: #990000; color: white; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: bold; }
        .error-content { flex: 1; }
        .error-title { font-weight: bold; color: #323334; font-size: 14px; margin-bottom: 2px; }
        .error-message { color: #4B4D4F; font-size: 14px; }
        .error-close { background: none; border: none; color: #4b4d4f; font-size: 18px; font-weight: bold; cursor: pointer; padding: 0; width: 20px; height: 20px; display: flex; align-items: center; justify-content: center; margin-left: auto; }
        .error-close:hover { background: rgba(102, 102, 102, 0.1); border-radius: 50%; }
        #attach-btn { background: transparent !important; border: none !important; outline: none !important; box-shadow: none !important; border-radius: 0 !important; width: 36px; height: 36px; display: none; align-items: center; justify-content: center; cursor: pointer; color: black; }
        #attach-btn:hover { color: #666; }
        .typing-indicator { position: absolute; bottom: 88px; left: 16px; right: 16px; padding: 0px 20px; background: white; display: flex; align-items: center; gap: 8px; font-size: 12px; color: #666; z-index: 10; }
        .typing-dots { display: flex; gap: 2px; }
        .typing-dots span { width: 4px; height: 4px; background: #666; border-radius: 50%; animation: typing 1.4s infinite; }
        .typing-dots span:nth-child(2) { animation-delay: 0.2s; }
        .typing-dots span:nth-child(3) { animation-delay: 0.4s; }
        @keyframes typing { 0%, 60%, 100% { transform: translateY(0); } 30% { transform: translateY(-10px); } }
        .message-status { font-size: 10px; color: black; margin-top: 4px; text-align: right; margin-left: auto; width: fit-content; }
        .chat-notification { color: black; padding: 8px 12px; text-align: center; margin: 8px 0; font-size: 12px; }
        .agent-joined-notification { color: black; padding: 8px 12px; text-align: center; margin: 8px 0; font-size: 12px; }
        .supervisor-notification { color: black; padding: 8px 12px; text-align: center; margin: 8px 0; font-size: 12px; font-weight: 500; }
        .chat-input-container input { flex: 1; border: none; outline: none; font-size: 14px; background: transparent; min-width: 100px; }
        .chat-input-container button { background: #196ECF; border: none; border-radius: 50%; width: 44px; height: 44px; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 2px 8px rgba(25,118,210,0.2); color: white; }
        .chat-input-container button:hover { background: #1565c0; transform: scale(1.05); }
        .chat-container.minimized .chat-messages,
        .chat-container.minimized .chat-input-container {
            display: none;
        }
        .menu-popup {
            position: absolute;
            top: 50px;
            left: 20px;
            background: white;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            z-index: 1002;
            min-width: 180px;
        }
        .menu-item {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 6px 16px;
            cursor: pointer;
            font-size: 14px;
            color: #333;
        }
        .menu-item:hover {
            background: #f5f5f5;
        }
        .menu-icon {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 20px;
            height: 20px;
        }
        @media print {
            body * {
                visibility: hidden;
            }
            .print-content, .print-content * {
                visibility: visible;
            }
            .print-content {
                position: absolute;
                left: 0;
                top: 0;
                width: 100%;
            }
        }
        .confirmation-modal { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: none; align-items: center; justify-content: center; z-index: 10; border-radius: 8px; }
        .confirmation-content { background: white; padding: 20px; border-radius: 8px; text-align: left; width: 331px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15); font-family: Arial, sans-serif; position: relative; }
        .modal-close-btn { position: absolute; top: 15px; right: 20px; background: none; border: none; font-size: 24px; color: #666; cursor: pointer; width: 30px; height: 30px; display: flex; align-items: center; justify-content: center; }
        .modal-close-btn:hover { color: #333; background: #f0f0f0; border-radius: 50%; }
        .confirmation-content h2 { font-size: 18px; font-weight: bold; margin-bottom: 8px; color: #002677; }
        .confirmation-content p { font-size: 14px; color: #555; margin-bottom: 16px; }
        .confirmation-buttons { display: flex; gap: 12px; }
        .confirm-btn { border: none; padding: 10px 18px; border-radius: 999px; font-size: 14px; cursor: pointer; white-space: nowrap; }
        .confirm-btn.yes { background-color: #002677; color: #fff; }
        .confirm-btn.no { background-color: #fff; color: #002677; border: 2px solid #002677; }
        .attachment-modal { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(0,0,0,0.5); display: none; align-items: center; justify-content: center; z-index: 9999; }
        .attachment-modal-content { background: white; border-radius: 8px; max-width: 500px; width: 90%; position: relative; display: flex; flex-direction: column; }
        .attachment-modal-header { padding: 20px; border-bottom: 1px solid #e0e0e0; display: flex; justify-content: space-between; align-items: center; }
        .attachment-modal-header h3 { margin: 0; font-size: 18px; font-weight: 600; }
        .attachment-modal-body { padding: 20px; flex: 1; }
        .attachment-modal-footer { padding: 20px; border-top: 1px solid #e0e0e0; display: flex; justify-content: flex-end; }
        .close-modal { background: none; border: none; font-size: 24px; color: #666; cursor: pointer; padding: 0; }
        .close-modal:hover { color: #333; }
        .file-drop-zone { border: 2px dashed #ddd; border-radius: 8px; padding: 40px 20px; text-align: center; background: #fafafa; }
        .file-drop-zone p { margin-bottom: 15px; color: #666; }
        .send-attachments-btn { background: #196ECF; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; }
        .send-attachments-btn:disabled { background: #ccc; cursor: not-allowed; }
        .send-attachments-btn:hover:not(:disabled) { background: #1565c0; }
        .attachment-content { background: white; padding: 40px; border-radius: 0; text-align: left; max-width: 700px; width: 90%; position: relative; }
        .attachment-content h2 { color: #003366; font-size: 28px; font-weight: bold; margin-bottom: 30px; margin-top: 0; font-family: serif; text-align: left; }
        .attachment-guidelines { text-align: left; margin: 20px 0; padding-left: 0; list-style: none; }
        .attachment-guidelines li { margin-bottom: 8px; font-size: 14px; color: #333; position: relative; padding-left: 25px; }
        .attachment-guidelines li::before { content: '✓'; position: absolute; left: 0; color: #003366; font-weight: bold; font-size: 16px; }
        .attachment-buttons { display: flex; gap: 12px; margin-top: 20px; justify-content: center; }
        .attachment-btn { padding: 10px 20px; border: none; border-radius: 6px; cursor: pointer; font-size: 14px; font-weight: 500; }
        .attachment-btn.primary { background: #196ECF; color: white; }
        .attachment-btn.primary:hover { background: #1565c0; }
        .attachment-btn.secondary { background: #6c757d; color: white; }
        .attachment-btn.secondary:hover { background: #5a6268; }
        .file-drop-area { background: #f5f5f5; border: 2px dashed #ddd; border-radius: 8px; padding: 30px 15px; margin: 20px 80px; text-align: center; }
        .drop-text { font-size: 14px; color: #333; margin-bottom: 10px; font-weight: bold; }
        .or-text { font-size: 14px; color: #333; margin: 6px 0; }
        .browse-btn { background: white; border: 2px solid #003366; color: #003366; padding: 10px 20px; border-radius: 25px; cursor: pointer; font-size: 14px; font-weight: bold; margin: 6px 0; }
        .browse-btn:hover { background: #f0f8ff; }
        .file-size-limit { font-size: 12px; color: #333; margin-top: 15px; }
        .close-btn { position: absolute; top: 15px; right: 20px; background: none; border: none; font-size: 24px; color: #666; cursor: pointer; width: 30px; height: 30px; display: flex; align-items: center; justify-content: center; }
        .close-btn:hover { color: #333; background: #f0f0f0; border-radius: 50%; }
        .inline-options { position: relative; margin-top: 1rem; display: flex; flex-direction: column; gap: 8px; align-items: flex-end; z-index: 10; flex-wrap: wrap; }
        .quick-replies { display: flex; flex-direction: column; gap: 4px; align-items: flex-end; justify-content: flex-start; flex-wrap: wrap;  margin-top:8px }
        .quick-reply-btn { background: transparent !important; border: 2px solid #003366 !important; border-radius: 30px !important; color: #003366 !important; font-weight: 500 !important; padding: 8px 16px !important; margin: 4px 2px !important; transition: all 0.3s ease !important; font-size: clamp(12px, 3vw, 14px) !important; cursor: pointer !important; box-shadow: 0 2px 4px rgba(0,51,102,0.2) !important; word-wrap: break-word !important; max-width: 100% !important; }
        .quick-reply-btn:hover { background: #003366 !important; color: white !important; transform: translateY(-1px) !important; box-shadow: 0 4px 8px rgba(0,51,102,0.3) !important; }
        .list-picker { display: flex; flex-direction: column; gap: 8px; }
        .list-option { background: #f5f5f5; border: 1px solid #ddd; border-radius: 8px; padding: 12px 16px; text-align: left; cursor: pointer; transition: all 0.3s ease; }
        .list-option:hover { background: #e3f2fd; border-color: #196ECF; }
        .message { margin: 1px 0; padding: 12px 24px; border-radius: 18px; max-width: min(80%, 300px); word-wrap: break-word; overflow-wrap: break-word; hyphens: auto; }
        .message.customer { background: #196ECF; color: white; align-self: flex-end; margin-left: auto; border-radius: 8px 8px 0px 8px; min-width: min(140px, 40vw); max-width: min(280px, 70vw); }
        .message-wrapper.customer-wrapper { justify-content: flex-end; width: 100%; }
        .message.customer.transcript-message { background: #196ECF; color: white; align-self: flex-end; margin-left: auto; border-radius: 8px 8px 0px 8px; min-width: min(140px, 40vw); max-width: min(280px, 70vw); }
        .customer-wrapper .message-content { align-items: flex-end; margin-left: auto; }
        .message.customer.file-message { background: transparent; padding: 0; width: min(100px, 25vw); max-width: min(200px, 50vw); margin-left: auto; margin-right: 0px; margin-top: 4px; }
        .file-thumbnails-chat { margin-left: auto; }
        .message.agent { background: #EDF3FB; color: #666; align-self: flex-start; border-radius: 0px 8px 8px 8px; min-width: min(140px, 40vw); max-width: min(280px, 70vw); }
        .message.system { background: #EDF3FB; color: #666; align-self: flex-start; border-radius: 0px 8px 8px 8px; max-width: min(90%, 350px); }
        .message.supervisor { background: #fff3e0; color: #666; align-self: flex-start; border-radius: 0px 8px 8px 8px; min-width: min(140px, 40vw); max-width: min(280px, 70vw); }
        .message-wrapper { display: flex; align-items: center; margin: 8px 0; gap: 8px; width: 90%; }
        .message-wrapper.customer-wrapper:has(.file-message) { margin: 0; }
        .bot-icon { display: flex; align-items: center; justify-content: center; width: 24px; height: 24px; background: #196ECF; color: white; border-radius: 50%; font-size: 12px; flex-shrink: 0; }
        .agent-icon { display: flex; align-items: center; justify-content: center; width: 32px; height: 32px; background: #196ECF; color: white; border-radius: 50%; font-size: 14px; font-weight: bold; flex-shrink: 0; }
        .supervisor-icon { display: flex; align-items: center; justify-content: center; width: 32px; height: 32px; background: #ff6b35; color: white; border-radius: 50%; font-size: 14px; font-weight: bold; flex-shrink: 0; }
        .agent-wrapper, .supervisor-wrapper { align-items: flex-start; }
        .customer-wrapper { align-items: flex-start; justify-content: flex-end; }
        .message-content { display: flex; flex-direction: column; gap: 4px; }
        .agent-info { font-size: 11px; color: #666; font-weight: 500; margin-left: 8px; }
        .supervisor-info { font-size: 11px; color: #ff6b35; font-weight: 500; margin-left: 8px; }
        .customer-info { font-size: 11px; color: #666; font-weight: 500; text-align: right; }
        .chat-container.minimized { height: 60px; overflow: hidden; }
        .chat-container.minimized .chat-messages, .chat-container.minimized .chat-input-container { display: none; }
        .file-thumbnail-chat { display: flex; align-items: center; justify-content: center; padding: 0; position: relative; width: 104px; height: 104px; }
        .file-thumbnails-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 0; width: 200px !important; }
        .file-thumbnails-chat { display: flex; flex-direction: column; gap: 0; width: 100px; }
        .file-preview-image { width: 104px; height: 104px; object-fit: cover; border-radius: 8px; border: 1px solid #ccc; }
        .file-loading-overlay { position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.4); border-radius: 8px; display: flex; align-items: center; justify-content: center; }
        .file-spinner { width: 20px; height: 20px; border: 2px solid rgba(255,255,255,0.3); border-top: 2px solid white; border-radius: 50%; animation: spin 1s linear infinite; }
        .file-failed-indicator { color: white; font-size: 16px; font-weight: bold; text-shadow: 0 1px 2px rgba(0,0,0,0.3); }
        @keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
        .file-thumbnail-preview { position: relative; width: 80px; height: 80px; border-radius: 4px; overflow: hidden; flex-shrink: 0; }
        .file-preview-container { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; }
        .file-preview-container .file-preview-image { width: 80px; height: 80px; object-fit: cover; border-radius: 4px; border: 1px solid #ccc; }
        .file-thumbnail-preview .remove-file { position: absolute; top: 4px; right: 4px; width: 24px; height: 24px; background: #666; color: white; border: none; border-radius: 50%; font-size: 16px; cursor: pointer; display: flex; align-items: center; justify-content: center; z-index: 10; }
        .agent-attachment-container { display: flex; flex-direction: column; gap: 12px; }
        .agent-attachment-item { display: flex; flex-direction: column; align-items: center; gap: 8px; }
        .agent-attachment-preview { width: 120px; height: 120px; object-fit: cover; border-radius: 8px; border: 1px solid #e0e0e0; }
        .agent-download-btn { background: white; color: #196ECF; border: 1px solid #196ECF; padding: 8px 16px; border-radius: 20px; font-size: 12px; font-weight: 500; cursor: pointer; transition: background 0.2s; }
        .agent-download-btn:hover { background: #f5f5f5; }
        .message.agent .attachment-container { background: transparent; border: none; padding: 0; }
        .file-thumbnail-chat { display: flex; align-items: center; justify-content: center; padding: 0; position: relative; width: 104px; height: 104px; }
        .file-thumbnails-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 0; width: 200px !important; }
        .file-thumbnails-chat { display: flex; flex-direction: column; gap: 0; width: 100px; }
        .file-preview-image { width: 104px; height: 104px; object-fit: cover; border-radius: 8px; border: 1px solid #ccc; }
        .file-icon { width: 104px; height: 104px; background: rgba(255,255,255,0.2); border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 24px; color: white; border: 1px solid #ccc; }
        .document-preview { width: 104px; height: 104px; background: white; border-radius: 8px; display: flex; flex-direction: column; align-items: center; justify-content: center; border: 1px solid #ccc; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .doc-icon { font-size: 20px; margin-bottom: 2px; }
        .doc-ext { font-size: 8px; font-weight: bold; color: #666; background: #f0f0f0; padding: 1px 4px; border-radius: 2px; }
        .file-loading-overlay { position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.4); border-radius: 8px; display: flex; align-items: center; justify-content: center; }
        .file-thumbnail-chat.has-error .file-loading-overlay { right: 20px; }
        .file-spinner { width: 20px; height: 20px; border: 2px solid rgba(255,255,255,0.3); border-top: 2px solid white; border-radius: 50%; animation: spin 1s linear infinite; }
        .file-failed-indicator { color: white; font-size: 16px; font-weight: bold; text-shadow: 0 1px 2px rgba(0,0,0,0.3); }
        .file-size-error-icon { position: absolute; top: 50%; right: -16px; transform: translateY(-50%); width: 0; height: 0; border-left: 8px solid transparent; border-right: 8px solid transparent; border-bottom: 16px solid #002677; display: flex; align-items: center; justify-content: center; color: white; font-size: 12px; font-weight: bold; }
        .file-size-error-icon::after { content: '!'; position: absolute; top: 8px; left: -1px; }
        .file-size-error-msg { position: absolute; top: 100%; left: 20px; right: 0; color: #323334; font-size: 16px; font-family: 'Enterprise Sans VP', Arial, sans-serif; font-weight: normal; text-align: left; margin-top: 8px; white-space: nowrap; }
        .file-thumbnail-preview { position: relative; width: 80px; height: 80px; border-radius: 4px; overflow: hidden; flex-shrink: 0; }
        .file-preview-container { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; }
        .file-preview-container .file-preview-image { width: 80px; height: 80px; object-fit: cover; border-radius: 4px; border: 1px solid #ccc; }
        .file-thumbnail-preview .remove-file { position: absolute; top: 4px; right: 4px; width: 24px; height: 24px; background: #666; color: white; border: none; border-radius: 50%; font-size: 16px; cursor: pointer; display: flex; align-items: center; justify-content: center; z-index: 10; }
        .attachment-container { display: flex; gap: 8px; margin-top: 0; }
        .attachment-item { display: flex; align-items: center; gap: 12px; padding: 12px; background: white; border: 1px solid #e0e0e0; border-radius: 8px; max-width: 280px; }
        .attachment-icon { font-size: 24px; width: 32px; height: 32px; display: flex; align-items: center; justify-content: center; background: #f5f5f5; border-radius: 6px; flex-shrink: 0; }
        .attachment-info { flex: 1; min-width: 0; }
        .attachment-name { font-weight: 500; font-size: 14px; color: #333; margin-bottom: 2px; word-break: break-word; }
        .attachment-size { font-size: 12px; color: #666; }
        .download-btn { background: #196ECF; color: white; border: none; padding: 6px 12px; border-radius: 6px; font-size: 12px; cursor: pointer; transition: background 0.2s; flex-shrink: 0; }
        .download-btn:hover { background: #1565c0; }
        .attachment-preview { width: 48px; height: 48px; object-fit: cover; border-radius: 6px; cursor: pointer; border: 1px solid #e0e0e0; transition: transform 0.2s; }
        .attachment-preview:hover { transform: scale(1.05); }
        .agent-attachment-container { display: flex; flex-direction: column; gap: 12px; }
        .agent-attachment-item { display: flex; flex-direction: column; align-items: center; gap: 8px; }
        .agent-attachment-preview { width: 120px; height: 120px; object-fit: cover; border-radius: 8px; border: 1px solid #e0e0e0; }
        .agent-download-btn { background: white; color: #196ECF; border: 1px solid #196ECF; padding: 8px 16px; border-radius: 20px; font-size: 12px; font-weight: 500; cursor: pointer; transition: background 0.2s; }
        .agent-download-btn:hover { background: #f5f5f5; }
        .survey-dropdown-container { display: flex; justify-content: center; margin-top: 8px; }
        .survey-combo { position: relative; width: 200px; }
        .survey-combo-input { width: 100%; padding: 12px 16px; border: 2px solid #196ECF; border-radius: 8px; font-size: 14px; background: white; color: #333; cursor: pointer; outline: none; transition: all 0.3s ease; box-shadow: 0 2px 4px rgba(25, 118, 210, 0.1); box-sizing: border-box; }
        .survey-combo-input:hover { border-color: #1565c0; box-shadow: 0 4px 8px rgba(25, 118, 210, 0.2); }
        .survey-combo-listbox { position: absolute; bottom: calc(100% + 4px); left: 0; right: 0; max-height: 360px; overflow-y: auto; border: 2px solid #196ECF; border-radius: 8px; background: white; box-shadow: 0 8px 16px rgba(0,0,0,0.12); z-index: 1000; display: none; }
        .survey-combo-option { padding: 8px 16px; cursor: pointer; font-size: 14px; color: #333; }
        .survey-combo-option[aria-selected="true"], .survey-combo-option:hover { background: #e3f2fd; }
        .survey-combo-open .survey-combo-listbox { display: block; }
        .upload-loading { font-size: 12px; color: #666; margin: 4px 0; text-align: right; display: flex; align-items: center; justify-content: flex-end; gap: 6px; }
        .loading-spinner { width: 12px; height: 12px; border: 2px solid #ddd; border-top: 2px solid #666; border-radius: 50%; animation: spin 1s linear infinite; }
        .file-size-error { background: #ffebee; color: #323334; padding: 10px; border-radius: 8px; margin: 10px 0; text-align: center; font-family: 'Enterprise Sans VP', Arial, sans-serif; font-weight: normal; font-size: 14px; border: 1px solid #ffcdd2; }
        .file-preview { background: #f5f5f5; border: 1px solid #ddd; border-radius: 8px; padding: 12px; margin: 8px 0; }
        .preview-files { margin-bottom: 10px; display: flex; flex-direction: column; gap: 8px; }
        .preview-file-item { display: flex; align-items: center; gap: 10px; padding: 8px; background: white; border-radius: 4px; border: 1px solid #eee; }
        .preview-thumbnail { width: 40px; height: 40px; object-fit: cover; border-radius: 4px; }
        .preview-file-icon { width: 40px; height: 40px; display: flex; align-items: center; justify-content: center; font-size: 20px; background: #e3f2fd; border-radius: 4px; }
        .preview-file-info { flex: 1; }
        .file-name { font-weight: bold; font-size: 14px; margin-bottom: 2px; }
        .file-size { font-size: 12px; color: #666; }
        .preview-buttons { display: flex; gap: 8px; justify-content: flex-end; }
        .preview-send, .preview-cancel { padding: 6px 12px; border: none; border-radius: 4px; cursor: pointer; font-size: 12px; }
        .preview-send { background: #196ECF; color: white; }
        .preview-cancel { background: #666; color: white; }
        .file-confirmation { background: #f5f5f5; border: 1px solid #ddd; border-radius: 8px; padding: 10px; margin: 8px 0; }
        .confirm-send, .confirm-cancel { padding: 4px 8px; border: none; border-radius: 4px; cursor: pointer; font-size: 12px; margin-right: 6px; }
        .confirm-send { background: #196ECF; color: white; }
        .confirm-cancel { background: #666; color: white; }
        .greeting { font-family: 'Times New Roman', serif; font-size: 22pt; font-weight: bold; color: #003366; margin: 20px 0; text-align: left; }
        .greeting-text { font-family: 'Times New Roman', serif; font-size: 15pt; color: #002D72; text-align: left; margin: 0 auto; }
        @media (max-width: 768px) {
            .chat-container { position: fixed; top: 0; left: 0; right: 0; bottom: 0; width: 100%; height: 100vh; border-radius: 0; }
            .survey-combo { width: min(180px, 80vw); }
            .survey-combo-input { font-size: 13px; padding: 10px 14px; }
            .quick-replies { flex-direction: column; align-items: stretch; }
            .quick-reply-btn { margin: 4px 0 !important; text-align: center !important; }
            .message { max-width: 90% !important; }
            .structured-message-wrapper.first-bot-message, .message-wrapper.first-bot-message { width: 100%; margin: 0.5rem auto; padding:0.5rem;}
        }
        @media (max-width: 480px) {
            .chat-container { width: 100vw; height: 100vh; bottom: 0; right: 0; }
            .quick-reply-btn { font-size: 12px !important; padding: 6px 12px !important; }
            .message { font-size: 14px; padding: 10px 12px; }
            .structured-message-wrapper.first-bot-message .message { font-size: 16px; }
        }
        @media (min-width: 1200px) and (max-height: 800px) {
            .chat-container { height: min(600px, 75vh); }
        }
        @media print {
            body * { visibility: hidden; }
            .print-content, .print-content * { visibility: visible; }
            .print-content { position: absolute; left: 0; top: 0; width: 100%; }
        }
    `;
    
    const styleSheet = document.createElement('style');
    styleSheet.textContent = styles;
    document.head.appendChild(styleSheet);
}

/**
 * Survey Handler for UHC Member Portal
 * Handles dropdown functionality specifically for survey questions
 */
class SurveyHandler {
    constructor(acChatWidget) {
        this.acChatWidget = acChatWidget;
        this.isSurveyQuestion = false;
    }

    /**
     * Check if the message content indicates a survey question
     */
    isSurveyContent(content) {
        // If we're in survey mode, treat ALL structured content as survey questions
        // until we see a completion message
        if (this.acChatWidget.surveyMode && !this.acChatWidget.surveyCompleted) {
            try {
                const tempDiv = document.createElement('div');
                tempDiv.innerHTML = content;
                const decodedContent = tempDiv.textContent || tempDiv.innerText || '';
                JSON.parse(decodedContent); // If it parses as JSON, it's structured content
                return true; // All structured content in survey mode gets dropdown
            } catch (e) {
                // Not structured content, check keywords
            }
        }
        
        const surveyKeywords = ['rate', 'rating', 'satisfaction', 'likely', 'recommend', 'experience', 'feedback', 'survey', 'scale', 'how would you', 'please rate', 'on a scale'];
        const lowerContent = content.toLowerCase();
        return surveyKeywords.some(keyword => lowerContent.includes(keyword));
    }

    /**
     * Check if we're using the survey flow (based on config)
     */
    isUsingSurveyFlow() {
        return this.acChatWidget.surveyMode || 
               (this.acChatWidget.contactId && this.acChatWidget.contactId.includes('survey'));
    }

    /**
     * Display survey message with dropdown instead of quick replies
     */
    displaySurveyMessage(content, participantRole, isFirstBotMessage = false) {
        try {
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = content;
            const decodedContent = tempDiv.textContent || tempDiv.innerText || '';
            const structured = JSON.parse(decodedContent);
            const messagesContainer = document.getElementById('chat-messages');
            
            // Create wrapper for survey message
            const surveyWrapper = document.createElement('div');
            surveyWrapper.className = 'structured-message-wrapper';
            
            // Remove any existing first-bot-message centering when survey starts
            const existingFirstMessage = messagesContainer.querySelector('.first-bot-message');
            if (existingFirstMessage) {
                existingFirstMessage.classList.remove('first-bot-message');
                existingFirstMessage.style.position = 'static';
                existingFirstMessage.style.transform = 'none';
            }
            
            // Ensure messages container is in normal flow
            messagesContainer.classList.add('has-messages');
            this.acChatWidget.firstBotMessage = false;
            
            // Create single message container that includes both question and dropdown
            const messageDiv = document.createElement('div');
            messageDiv.className = `message ${participantRole === 'AGENT' ? 'agent' : 'system'} survey-message`;
            
            // Add question text (no greeting in survey questions)
            const questionText = document.createElement('div');
            const messageContent = structured.data.content.title;
            questionText.textContent = messageContent;
            messageDiv.appendChild(questionText);
            
            // Add custom combobox inside the same message container
            if (structured.data.content.elements) {
                const combo = document.createElement('div');
                combo.className = 'survey-combo survey-dropdown-container';
                
                const input = document.createElement('input');
                input.type = 'text';
                input.className = 'survey-combo-input';
                input.placeholder = 'Select a rating';
                input.readOnly = true;
                input.setAttribute('role', 'combobox');
                
                const listbox = document.createElement('ul');
                listbox.className = 'survey-combo-listbox';
                listbox.setAttribute('role', 'listbox');
                
                // Add rating options (0-10)
                for (let i = 0; i <= 10; i++) {
                    const option = document.createElement('li');
                    option.className = 'survey-combo-option';
                    option.dataset.value = i.toString();
                    option.textContent = i === 0 ? `${i} - Least likely` : 
                                        i === 10 ? `${i} - Most likely` : i.toString();
                    listbox.appendChild(option);
                }
                
                combo.appendChild(input);
                combo.appendChild(listbox);
                messageDiv.appendChild(combo);
                
                // Event handlers
                input.addEventListener('click', () => {
                    combo.classList.toggle('survey-combo-open');
                });
                
                listbox.addEventListener('click', (e) => {
                    const opt = e.target.closest('.survey-combo-option');
                    if (opt) {
                        input.value = opt.textContent.trim();
                        combo.classList.remove('survey-combo-open');
                        this.sendSurveyResponse(opt.dataset.value);
                    }
                });
                
                document.addEventListener('click', (e) => {
                    if (!combo.contains(e.target)) combo.classList.remove('survey-combo-open');
                });
            }            
            surveyWrapper.appendChild(messageDiv);
            messagesContainer.appendChild(surveyWrapper);            
            requestAnimationFrame(() => {
                messagesContainer.scrollTop = messagesContainer.scrollHeight;
            });
            
        } catch (error) {
            console.error('[ChatWidget] Error displaying survey message:', error);
            this.acChatWidget.displayStructuredMessage(content, participantRole, isFirstBotMessage);
        }
    }

    /**
     * Send survey response
     */
    async sendSurveyResponse(rating) {
        if (!this.acChatWidget.chatSession) return;
        try {
            // Display customer message
            this.acChatWidget.displayCustomerMessage(rating);
            // Send through Amazon Connect
            await this.acChatWidget.chatSession.sendMessage({
                message: rating,
                contentType: 'text/plain'
            });
        } catch (error) {
            console.error('[ChatWidget] Failed to send survey response:', error);
        }
    }

}
// Initialize immediately and ensure DOM is ready
function initializeChatWidget() {
    console.log('[ChatWidget] Initializing widget, readyState:', document.readyState);
    if (!window.acChatWidget) {
        console.log('[ChatWidget] Creating new widget instance');
        injectStyles();
        window.acChatWidget = new ConnectChatWidget();
        console.log('[ChatWidget] Widget created and assigned to window.acChatWidget');
    } else {
        console.log('[ChatWidget] Widget already exists');
    }
}
 
// Initialize when DOM is ready
console.log('[ChatWidget] Script loaded, document.readyState:', document.readyState);
if (document.readyState === 'loading') {
    console.log('[ChatWidget] DOM still loading, adding event listener');
    document.addEventListener('DOMContentLoaded', initializeChatWidget);
} else {
    console.log('[ChatWidget] DOM ready, initializing immediately');
    initializeChatWidget();
}