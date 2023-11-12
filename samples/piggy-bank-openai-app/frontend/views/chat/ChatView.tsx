import { Button } from '@hilla/react-components/Button.js';
//https://hilla.dev/docs/react/components/message-list
import {MessageList, MessageListItem} from "@hilla/react-components/MessageList";
//https://hilla.dev/docs/react/components/message-input
import { MessageInput } from '@hilla/react-components/MessageInput';
import { Notification } from '@hilla/react-components/Notification.js';
import { TextField } from '@hilla/react-components/TextField.js';
import { sayHello } from 'Frontend/generated/HelloWorldService';
import { HelloWorldService } from 'Frontend/generated/endpoints.js';
import { useState } from 'react';

export default function ChatView() {
    const [messages, setMessages] = useState<MessageListItem[]>([]);

    function addMessage(message: MessageListItem) {
        setMessages([...messages, message]);
    }

    async function sayHello(name: string) {
        addMessage({ userName: 'You', text: name });

        const message = await HelloWorldService.sayHello(name);
        addMessage({ userName: 'Bot', text: message });
    }

    return (
      <div className="p-m flex flex-col h-full box-border">
        <MessageList items={messages} />
        <MessageInput onSubmit={e => sayHello(e.detail.value)} />
      </div>
    );
}