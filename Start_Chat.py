import json
import os
import time
from aws_lambda_powertools import Logger
from botocore.exceptions import ClientError
import boto3
import datetime
from boto3.dynamodb.conditions import Key
from typing import Any, Dict

LOG_LEVEL = os.environ.get('LOG_LEVEL', 'INFO')

# Configure logger
logger = Logger(level=LOG_LEVEL)

TABLE_NAME = os.environ.get('CHAT_CONTACTS_TABLE', '').strip()
REGION = os.environ.get('REGION')
INSTANCE_ID = os.environ.get('CONNECT_INSTANCE_ID')
FLOW_ID = os.environ.get('CONNECT_FLOW_ID')
SURVEY_ID = os.environ.get('CONNECT_SURVEY_ID')

# Initialize AWS clients
dynamodb_client = boto3.resource('dynamodb', region_name=REGION)
connect_client = boto3.client('connect', region_name=REGION)
table = dynamodb_client.Table(TABLE_NAME.strip()) if TABLE_NAME else None

def getHeaderValue(headers, header_name):
    for key, value in headers.items():
        if key.lower() == header_name.lower():
            return value
    return None

def lambda_handler(event, context):
    logger.append_keys(request_id=context.aws_request_id)
    logger.info(f'Start Chat session request received from {getHeaderValue(event.get("headers", {}), "User-Agent")}')
    
    try:        
        logger.info(f'Received event: {json.dumps(event, indent=2)}')
        
        http_method = event.get('httpMethod') or event.get('requestContext', {}).get('http', {}).get('method', '').upper()
        path = event.get('path') or event.get('rawPath', '/')
        
        logger.info(f'Processing request - Method: {http_method}, Path: {path}')

        logger.info(f'=== DEBUGGING SESSION FLOW === Method: {http_method}, Path: {path}')
        logger.info(f'Table configured: {bool(table)}, Table name: {TABLE_NAME}')
        
        # Handle GET request for retrieving active chat session
        if http_method == 'GET' and '/chat-session/' in path:
            # Extract client_login_id from path - handle both API Gateway and Function URL formats
            client_login_id = None
            
            # Try path parameters first (API Gateway format)
            if event.get('pathParameters'):
                client_login_id = event.get('pathParameters', {}).get('client_login_id')
            
            # If not found, try extracting from path directly (Function URL format)
            if not client_login_id and '/chat-session/' in path:
                path_parts = path.split('/chat-session/')
                if len(path_parts) > 1 and path_parts[1]:
                    client_login_id = path_parts[1].split('/')[0]  # Get first part after chat-session/
            
            logger.info(f'=== GET SESSION REQUEST === Extracted client_login_id: {client_login_id} from path: {path}')
            logger.info(f'Event pathParameters: {event.get("pathParameters")}')
            
            if not client_login_id:
                logger.warning(f'GET request missing client_login_id parameter. Path: {path}, PathParams: {event.get("pathParameters")}')
                return {
                    'statusCode': 400,
                    'body': json.dumps({'error': 'client_login_id is required'})
                }
            
            logger.info(f'Querying for active session for client: {client_login_id}')
            session_data = get_active_chat_session(client_login_id)
            logger.info(f'Session query result: {session_data}')
            
            if session_data:
                # Check if the session is actually still active in Connect
                try:
                    contact_id = session_data.get('contact_id')
                    logger.info(f'Found session with contact_id: {contact_id}, checking Connect status')
                    
                    if contact_id:
                        # Try to get contact details to verify if still active
                        contact_response = connect_client.describe_contact(
                            InstanceId=INSTANCE_ID,
                            ContactId=contact_id
                        )
                        
                        contact_state = contact_response.get('Contact', {}).get('State')
                        logger.info(f'Connect contact state: {contact_state} for contact: {contact_id}')
                        
                        # If contact is in a terminal state, mark session as ended
                        if contact_state in ['ENDED', 'ERROR', 'MISSED']:
                            logger.info(f'Contact {contact_id} is in terminal state {contact_state}, marking session as ended')
                            update_chat_session_status(client_login_id, 'ENDED')
                            return {
                                'statusCode': 404,
                                'body': json.dumps({'message': 'No active session found'})
                            }
                        
                        logger.info(f'Returning active session for reconnection: {contact_id}')
                        return {
                            'statusCode': 200,
                            'body': json.dumps({
                                'contactId': session_data.get('contact_id'),
                                'participantId': session_data.get('participantId'),
                                'participantToken': session_data.get('participantToken'),
                                'status': session_data.get('status')
                            })
                        }
                except Exception as e:
                    logger.error(f'Error checking contact status for {contact_id}: {str(e)}')
                    # If we can't verify the contact, assume it's ended
                    update_chat_session_status(client_login_id, 'ENDED')
                    return {
                        'statusCode': 404,
                        'body': json.dumps({'message': 'No active session found'})
                    }
            else:
                logger.info(f'No active session found for client: {client_login_id}')
                return {
                    'statusCode': 404,
                    'body': json.dumps({'message': 'No active session found'})
                }
        
        # Handle OPTIONS preflight request
        if http_method == 'OPTIONS':
            return {
                'statusCode': 200,
                'body': ''
            }
        
        # Handle DELETE/PUT request for ending chat session
        if http_method in ['DELETE', 'PUT'] and ('chat-session/end' in path or path.endswith('/chat-session/end')):
            try:
                # Handle Function URL event format - body might be base64 encoded
                body_data = event.get('body', '{}')
                is_base64 = event.get('isBase64Encoded', False)
                
                if is_base64 and body_data:
                    import base64
                    body_data = base64.b64decode(body_data).decode('utf-8')
                
                if isinstance(body_data, str) and body_data:
                    body = json.loads(body_data)
                else:
                    body = body_data or {}
                
                logger.info(f'Raw body from event: {event.get("body")}')
                logger.info(f'Parsed body: {body}')
                
                contact_id = body.get('contactId')
                client_login_id = body.get('clientLoginId')
                
                # If no body data, try to get from query parameters or path
                if not contact_id and not client_login_id:
                    query_params = event.get('queryStringParameters') or {}
                    contact_id = query_params.get('contactId')
                    client_login_id = query_params.get('clientLoginId')
                
                logger.info(f'End chat request received - contactId: {contact_id}, clientLoginId: {client_login_id}')
                
                # Don't require contactId - just update session status if clientLoginId exists
                if client_login_id:
                    logger.info(f'=== CLEANUP REQUEST === Updating session status to ENDED for client: {client_login_id}')
                    update_result = update_chat_session_status(client_login_id, 'ENDED')
                    logger.info(f'Update result: {update_result}')
                    logger.info(f'Chat session status updated to ENDED for client: {client_login_id}')
                    return {
                        'statusCode': 200,
                        'body': json.dumps({'message': 'Chat session ended successfully'})
                    }
                else:
                    # Even without client_login_id, return success for cleanup
                    logger.info('End chat request without client_login_id - returning success for cleanup')
                    return {
                        'statusCode': 200,
                        'body': json.dumps({'message': 'Chat session cleanup completed'})
                    }
                
            except Exception as e:
                logger.error(f'Error in end chat endpoint: {str(e)}')
                return {
                    'statusCode': 500,
                    'body': json.dumps({'error': 'Internal server error'})
                }
        
        # Handle ALB Health Check (GET request to /health)
        if http_method == 'GET' and path in ['/', '/health', '/HEALTHCHECK']:
            timestamp = datetime.datetime.utcnow().isoformat() + "Z"
            return {
                'statusCode': 200,
                'body': json.dumps({'message': 'OK', 'timestamp': timestamp})
            }
        
        # Handle POST request for starting chat (existing logic)
        logger.info('=== POST START CHAT REQUEST ===')
        body = json.loads(event['body']) if isinstance(event.get('body'), str) else event.get('body', {})
        logger.info(f'Request body: {json.dumps(body, indent=2)}')
        
        attributes = body.get('attributes', {})
        customer_language = body.get('customerLanguage', 'en')        
        
        if not INSTANCE_ID or not FLOW_ID:
            logger.error(f'Missing configuration - INSTANCE_ID: {INSTANCE_ID}, FLOW_ID: {FLOW_ID}')
            return {
                'statusCode': 400,
                'body': json.dumps({'error': 'Missing instanceId or contactFlowId, please check configuration'})
            }
        
        personal_info = attributes.get('PersonalInfo', {})
        customer_info = attributes.get('CustomerInfo', {})        
        # Extract email and first name from nested or flat structure
        email = personal_info.get('ENT_FromAddress') or attributes.get('ENT_FromAddress')
        first_name = personal_info.get('ENT_ContactFirstNm') or attributes.get('ENT_ContactFirstNm')
        last_name = personal_info.get('ENT_ContactLastNm') or attributes.get('ENT_ContactLastNm')
        client_login_id = customer_info.get('ENT_ClientLoginID', {}).get('username', None)

        logger.info(f'Extracted customer info - first_name: {first_name}, last_name: {last_name}, email: {email}, client_login_id: {client_login_id}')

        # Check for active chat session and existing contact history
        active_session = None
        existing_contact_id = None
        
        logger.info(f'Checking for existing sessions - survey mode: {body.get("type") == "survey"}, client_login_id: {client_login_id}')
        
        if body.get('type') != 'survey' and client_login_id:
            logger.info(f'=== CHECKING FOR ACTIVE SESSION === client_login_id: {client_login_id}')
            # 1. Check if user has an active chat session
            active_session = get_active_chat_session(client_login_id)
            logger.info(f'Active session check result: {active_session}')
            
            if active_session:
                # Return existing session for reconnection
                logger.info(f"Found active session - returning for reconnection. Contact ID: {active_session.get('contact_id')}")
                return {
                    'statusCode': 200,
                    'body': json.dumps({
                        'contactId': active_session.get('contact_id'),
                        'participantId': active_session.get('participantId'),
                        'participantToken': active_session.get('participantToken'),
                        'reconnect': True
                    })
                }
            
            # 2. Check for existing contact history for persistent chat
            logger.info(f'No active session found, checking for contact history for persistent chat')
            existing_contact_id = query_existing_contact_history(client_login_id)
            logger.info(f'Contact history check result: {existing_contact_id}')
        else:
            logger.info('Skipping session checks - either survey mode or no client_login_id')
            
        # Flatten all nested attributes for direct access in contact flow
        flattened_attributes = flatten_attributes(attributes, customer_language, first_name, last_name, email, client_login_id, context.aws_request_id)
        
        # 3. Start new chat with or without persistent chat
        params = {
            'InstanceId': INSTANCE_ID,
            'ContactFlowId': SURVEY_ID if (body.get('type') == 'survey') else FLOW_ID,
            'Attributes': flattened_attributes,
            'ParticipantDetails': {
                'DisplayName': f"{first_name or ''} {last_name or ''}".strip() or 'Customer'
            }
        }
        
        # Add PersistentChat only if we have existing contact history (not active session)
        if existing_contact_id:
            params['PersistentChat'] = {
                'SourceContactId': existing_contact_id,
                'RehydrationType': 'ENTIRE_PAST_SESSION'
            }
            logger.info("Starting new chat with persistent history", extra={"existing_contact_id": existing_contact_id})
        else:
            logger.info("Starting fresh chat without persistent history")
        
        # Start chat contact with retry logic for active chat conflicts
        response = start_chat_with_retry(params, existing_contact_id)
        
        response_body = {
            'contactId': response['ContactId'],
            'participantId': response['ParticipantId'],
            'participantToken': response['ParticipantToken']
        }

        logger.info("Chat Session created with parameters", extra={"contactID": response['ContactId'],
                                                                   "lambda_requestID": context.aws_request_id})
        
        # Store session data to database
        if client_login_id:
            store_chat_session(client_login_id, response['ContactId'], response['ParticipantId'], response['ParticipantToken'])
        
        # Include persistedChatSession if present
        if response.get('ContinuedFromContactId'):
            response_body['persistedChatSession'] = response['ContinuedFromContactId']
            logger.info("Included persistedChatSession in response", extra={"persistedChatSession": response['ContinuedFromContactId']})

        logger.info("Chat session successfully created", extra={"contact_id": response['ContactId'],
                                                                "rehydrated": bool(response.get('ContinuedFromContactId'))})
        
        return {
            'statusCode': 200,
            'body': json.dumps(response_body)
        }
        
    except json.JSONDecodeError as json_error:
        logger.error(f'Invalid JSON in request body: {json_error}')
        return {
            'statusCode': 400,
            'body': json.dumps({'error': 'Invalid JSON in request body'})
        }

    except ClientError as client_error:
        error_code = client_error.response['Error']['Code']
        logger.error("AWS Service Error", extra={"error_code": error_code,
                                                 "error_message": client_error.response['Error']['Message']})
        return {
            'statusCode': 500,
            'body': json.dumps({'error': f'service error: {error_code}'})
        }
    
    except Exception as error:
        logger.error(f'Unexpected error: {error}')
        return {
            'statusCode': 500,
            'body': json.dumps({'error': 'Internal Server Error'})
        }

def get_active_chat_session(client_login_id):
    """Get active chat session from DynamoDB"""
    logger.info(f'=== GET_ACTIVE_CHAT_SESSION === Searching for client: {client_login_id}')
    
    if not table:
        logger.error('DynamoDB table not configured')
        return None
        
    try:
        # First try using GSI (Global Secondary Index)
        try:
            logger.info(f'Trying GSI query on ClientLoginIndex for client: {client_login_id}')
            response = table.query(
                IndexName='ClientLoginIndex',
                KeyConditionExpression=Key('client_login_id').eq(client_login_id),
                FilterExpression='#status = :status',
                ExpressionAttributeNames={'#status': 'status'},
                ExpressionAttributeValues={':status': 'ACTIVE'},
                ScanIndexForward=False,
                Limit=1
            )
            
            items = response.get('Items', [])
            logger.info(f'GSI query returned {len(items)} items: {items}')
            
            if items:
                result = items[0]
                logger.info(f'Found session via GSI: {result}')
                return result
                
        except Exception as gsi_error:
            logger.warning(f'GSI query failed, trying scan: {str(gsi_error)}')
            
        # Fallback to scan if GSI doesn't exist
        logger.info(f'Falling back to scan for client: {client_login_id}')
        response = table.scan(
            FilterExpression='client_login_id = :client_id AND #status = :status',
            ExpressionAttributeNames={'#status': 'status'},
            ExpressionAttributeValues={
                ':client_id': client_login_id,
                ':status': 'ACTIVE'
            },
            Limit=10
        )
        
        items = response.get('Items', [])
        logger.info(f'Scan returned {len(items)} items: {items}')
        
        result = items[0] if items else None
        logger.info(f'Returning session data from scan: {result}')
        return result
        
    except Exception as e:
        logger.error(f'Error getting active session for {client_login_id}: {str(e)}')
        return None

def store_chat_session(client_login_id, contact_id, participant_id, participant_token):
    """Store chat session in DynamoDB"""
    if not table:
        return
        
    try:
        table.put_item(
            Item={
                'contact_id': contact_id,
                'client_login_id': client_login_id,
                'participantId': participant_id,
                'participantToken': participant_token,
                'status': 'ACTIVE',
                'created_at': datetime.datetime.utcnow().isoformat(),
                'ttl': int(time.time()) + (24 * 60 * 60)  # 24 hours TTL
            }
        )
        logger.info(f'Chat session stored for client: {client_login_id}')
        
    except Exception as e:
        logger.error(f'Error storing chat session: {str(e)}')

def update_chat_session_status(client_login_id, status):
    """Update chat session status in DynamoDB"""
    logger.info(f'=== UPDATE_CHAT_SESSION_STATUS === client: {client_login_id}, status: {status}')
    
    if not table:
        logger.error('DynamoDB table not configured for update')
        return False
        
    try:
        # Get the active session first
        logger.info(f'Getting active session before update for client: {client_login_id}')
        active_session = get_active_chat_session(client_login_id)
        logger.info(f'Active session for update: {active_session}')
        
        if active_session:
            contact_id = active_session['contact_id']
            logger.info(f'Updating session status to {status} for contact_id: {contact_id}')
            
            table.update_item(
                Key={'contact_id': contact_id},
                UpdateExpression='SET #status = :status, updated_at = :updated_at',
                ExpressionAttributeNames={'#status': 'status'},
                ExpressionAttributeValues={
                    ':status': status,
                    ':updated_at': datetime.datetime.utcnow().isoformat()
                }
            )
            logger.info(f'Successfully updated chat session status to {status} for client: {client_login_id}')
            return True
        else:
            logger.warning(f'No active session found to update for client: {client_login_id}')
            return False
        
    except Exception as e:
        logger.error(f'Error updating chat session status for {client_login_id}: {str(e)}')
        return False

def query_existing_contact_history(client_login_id):
    """Query for existing contact history for persistent chat"""
    if not table:
        return None
        
    try:
        response = table.query(
            IndexName='ClientLoginIndex',
            KeyConditionExpression=Key('client_login_id').eq(client_login_id),
            FilterExpression='#status = :status',
            ExpressionAttributeNames={'#status': 'status'},
            ExpressionAttributeValues={':status': 'ENDED'},
            ScanIndexForward=False,
            Limit=1
        )
        
        items = response.get('Items', [])
        return items[0]['contact_id'] if items else None
        
    except Exception as e:
        logger.error(f'Error querying contact history: {str(e)}')
        return None

def flatten_attributes(attributes, customer_language, first_name, last_name, email, client_login_id, lambda_request_id):
    """Flatten attributes for Connect contact flow"""
    
    flattened_attributes = {
        'customerLanguage': customer_language or '',
        'ENT_ContactFirstNm': first_name or '',
        'ENT_ContactLastNm': last_name or '',
        'ENT_FromAddress': email or '',
        'ENT_ClientLoginID_UserName': client_login_id or '',
        'lambda_requestID': lambda_request_id or ''
    }

    # Flatten additional attributes if needed
    for key, value in attributes.items():
        if isinstance(value, dict):
            for sub_key, sub_value in value.items():
                flattened_attributes[sub_key] = str(sub_value) if sub_value is not None else ''
            
            flattened_attributes[key] = json.dumps(value)
        else:
            flattened_attributes[key] = str(value) if value is not None else ''

    logger.debug("Flattened attributes", extra={"flattened_attributes_count": len(flattened_attributes)})

    return flattened_attributes

def start_chat_with_retry(params, existing_contact_id):
    """Start chat contact with retry logic for active chat conflicts"""
    
    try:
        logger.debug("Starting chat contact with retry logic")
        response = connect_client.start_chat_contact(**params)
        logger.info("Chat started successfully on first attempt",
                    extra={"contact_id": response['ContactId']})
        return response
    
    except ClientError as e:
        logger.error("AWS Client Error in start chat contact",
                     extra={"error_message": str(e)})
        error_message = str(e)

        if 'Active chats cannot be rehydrated' in error_message:
            logger.info("Active chat detected - starting new session without rehydration",
                        extra={"previous_contact_id": existing_contact_id})

            params.pop('PersistentChat', None)
            response = connect_client.start_chat_contact(**params)

            logger.info("New Chat session started", extra={"contact_id": response['ContactId']})
            return response
        else:
            logger.error("Connect API error", extra={"error_message": error_message})
            raise
    
    except Exception as e:
        logger.error("Unexpected error in start_chat_with_retry", extra={"error_message": str(e)})
        raise