import React, { useState, useEffect } from 'react';
import {
  Breadcrumb,
  BreadcrumbItem,
  Button,
  Tabs,
  Tab,
  TextInput,
  ToastNotification
} from 'carbon-components-react';
import axios from 'axios';

const props = {
  tabs: {
    selected: 0,
    triggerHref: '#',
    role: 'navigation',
  },
  tab: {
    href: '#',
    role: 'presentation',
    tabIndex: 0,
  },
};

const MqPage = () => {
  const [testMessage, setTestMessage] = useState('');
  const [toastProps, setToastProps] = useState([]);

  const arePropsEqual = (a, b) => {
    // Create arrays of property names
    let aProps = Object.getOwnPropertyNames(a);
    let bProps = Object.getOwnPropertyNames(b);

    // If number of properties is different,
    // objects are not equivalent
    if (aProps.length !== bProps.length) {
      return false;
    }

    for (let i = 0; i < aProps.length; i++) {
      let propName = aProps[i];

      // If values of same property are not equal,
      // objects are not equivalent
      if (a[propName] !== b[propName]) {
        return false;
      }
    }

    // If we made it this far, objects
    // are considered equivalent
    return true;
  };

  const showAndHideToast = async (props) => {
    showToast(props);
    await new Promise((resolve) => setTimeout(resolve, 5000));
    hideToast(props);
  };

  const showToast = (props) => {
    setToastProps(currentProps => currentProps.concat([props]));
  };

  const hideToast = (props) => {
    setToastProps(currentProps => currentProps.filter(prop => !arePropsEqual(prop, props)));
  };

  const sendMQMessage = async (message) => {
    const response = await axios.post(`${window.location.origin}/jms/rest/mq/send`, message);
    const result = response.data;
    const responseProps = {
      title: 'Status',
      subtitle: 'Remote queue',
      caption: result[0],
      kind: 'success'
    };

    await showAndHideToast(responseProps);
  };

  const receiveMQMessage = async () => {
    const response = await axios.get(`${window.location.origin}/jms/rest/mq/receive`);
    let props = {};
    if (response.data.toString().indexOf('error') !== -1) {
      props = {
        title: 'Receive Failed',
        subtitle: 'Remote queue',
        caption: response.data,
        kind: 'error'
      }
    } else {
      props = {
        title: 'Response',
        subtitle: 'Remote queue',
        caption: response.data[0],
        kind: 'success'
      }
    }

    await showAndHideToast(props);
  };

  const sendMQTopicMessage = async (message) => {
    const response = await axios.post(`${window.location.origin}/jms/rest/mq/topic/send`, message);
    const result = response.data;
    const responseProps = {
      title: 'Status',
      subtitle: 'Remote topic',
      caption: result[0],
      kind: 'success'
    };

    await showAndHideToast(responseProps);
  };

  const receiveMQTopicMessage = async () => {
    const response = await axios.get(`${window.location.origin}/jms/rest/mq/topic/receive`);
    let props = {};
    if (response.data.toString().indexOf('error') !== -1) {
      props = {
        title: 'Receive Failed',
        subtitle: 'Remote topic',
        caption: response.data,
        kind: 'error'
      }
    } else {
      props = {
        title: 'Response',
        subtitle: 'Remote topic',
        caption: response.data[0],
        kind: 'success'
      }
    }

    await showAndHideToast(props);
  };

  const produceMQMessageForBean = async (message) => {
    const response = await axios.post(`${window.location.origin}/jms/rest/mq/mdb/do_message/`, message);
    const result = response.data;
    const responseProps = {
      title: 'Status',
      subtitle: 'MQ message producer',
      caption: result[0],
      kind: 'success'
    };

    await showAndHideToast(responseProps)
  };

  const renderToast = () => {
    return (
      <div>
        {toastProps.map((prop) => (
          <ToastNotification {...prop} />
        ))}
      </div>
    )
  };

  return (
    <div className="bx--grid bx--grid--full-width mq-page">
      <div className="bx--row mq-page__banner">
        <div className="bx--col-lg-16">
          <Breadcrumb noTrailingSlash aria-label="Page navigation">
            <BreadcrumbItem>
              <a href="/">MQ Funhouse</a>
            </BreadcrumbItem>
          </Breadcrumb>
          <h1 className="mq-page__heading">
            Test MQ Resources
          </h1>
        </div>
      </div>
      <div className="bx--row mq-page__r2">
        <div className="bx--col bx--no-gutter">
          <Tabs {...props.tabs} aria-label="Tab navigation">
            <Tab {...props.tab} label="IBM MQ Queue">
              <div className="bx--grid bx--grid--no-gutter bx--grid--full-width">
                <div className="bx--row mq-page__tab-content">
                  <div className="bx--col-md-4 bx--col-lg-7">
                    <h2 className="mq-page__subheading">
                      MQ Queue Test
                    </h2>
                    <p className="mq-page__p">
                      Send messages to MQ, then retrieve them. The queue is accessed remotely over the MQ endpoint.
                    </p>
                    <div className="mq-page__form">
                      <TextInput labelText={'Message to send'} id={'mqTestText'} value={testMessage}
                                 onChange={(e) => setTestMessage(e.target.value)} />
                      <div className="mq-page__button-array">
                        <Button onClick={() => sendMQMessage(testMessage)} disabled={testMessage.length === 0}>Send message to remote queue</Button>
                        <Button onClick={() => receiveMQMessage()} >Attempt to receive message from remote queue</Button>
                      </div>

                    </div>
                  </div>
                  <div className="bx--col-md-4 bx--offset-lg-1 bx--col-lg-8">
                    <img
                      className="mq-page__illo"
                      src={`${process.env.PUBLIC_URL}/tab-illo.png`}
                      alt="Carbon illustration"
                    />
                  </div>
                </div>
              </div>
            </Tab>
            <Tab {...props.tab} label="IBM MQ Topic">
              <div className="bx--grid bx--grid--no-gutter bx--grid--full-width">
                <div className="bx--row mq-page__tab-content">
                  <div className="bx--col-md-4 bx--col-lg-7">
                    <h2 className="mq-page__subheading">
                      MQ Topic Test
                    </h2>
                    <p className="mq-page__p">
                      Publish messages to MQ, then subscribe to them. The topic is accessed remotely over the MQ endpoint.
                    </p>
                    <div className="mq-page__form">
                      <TextInput labelText={'Message to send'} id={'mqTestText'} value={testMessage}
                                 onChange={(e) => setTestMessage(e.target.value)} />
                      <div className="mq-page__button-array">
                        <Button onClick={() => sendMQTopicMessage(testMessage)} disabled={testMessage.length === 0}>Publish message to remote topic</Button>
                        <Button onClick={() => receiveMQTopicMessage()} >Attempt to subscribe to message from remote topic</Button>
                      </div>

                    </div>
                  </div>
                  <div className="bx--col-md-4 bx--offset-lg-1 bx--col-lg-8">
                    <img
                      className="mq-page__illo"
                      src={`${process.env.PUBLIC_URL}/tab-illo.png`}
                      alt="Carbon illustration"
                    />
                  </div>
                </div>
              </div>
            </Tab>
            <Tab {...props.tab} label="MQ MDB">
              <div className="bx--grid bx--grid--no-gutter bx--grid--full-width">
                <div className="bx--row mq-page__tab-content">
                  <div className="bx--col-md-4 bx--col-lg-7">
                    <h2 className="mq-page__subheading">
                      MQ Message-driven bean test
                    </h2>
                    <p className="mq-page__p">
                      This will send a message to MQ; an MDB should be activated and print a message to the server logs.
                    </p>
                    <div className="mq-page__form">
                      <TextInput labelText={'Message to send'} id={'mqMdbTestText'} value={testMessage}
                                 onChange={(e) => setTestMessage(e.target.value)}/>
                      <Button onClick={() => produceMQMessageForBean(testMessage)} disabled={testMessage.length === 0}>Create MQ message for bean</Button>
                    </div>
                  </div>
                  <div className="bx--col-md-4 bx--offset-lg-1 bx--col-lg-8">
                    <img
                      className="mq-page__illo"
                      src={`${process.env.PUBLIC_URL}/tab-illo.png`}
                      alt="Carbon illustration"
                    />
                  </div>
                </div>
              </div>
            </Tab>
          </Tabs>
        </div>
      </div>
      <div className="mq-page__toast">
        { renderToast() }
      </div>
    </div>

  );
};

export default MqPage;
