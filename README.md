# recognizers-service

This is Microsoft's Recognizers-Text [Java library](https://github.com/microsoft/Recognizers-Text/tree/master/Java) packaged into a REST service. It is useful to extract information, such as numbers and time, from a text sentence. Being in a REST service makes it easy to consume in a cloud native environment, or chatbot development.

Once it stands up, the port 7000 will be activated.

```shell
docker run -p 7000:7000 xanthoustech/recognizers-service
```

There is a swagger UI for API specifications at http://localhost:7000/swagger-ui or https://localhost:7000/redoc

## Features

Currently, it supports

- Number
- Currency
- Dimensions
- Number Range
- Currency Range
- Dimension Range

# About Micronaut

## Micronaut 2.4.0 Documentation

- [User Guide](https://docs.micronaut.io/2.4.0/guide/index.html)
- [API Reference](https://docs.micronaut.io/2.4.0/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/2.4.0/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

## Feature http-client documentation

- [Micronaut HTTP Client documentation](https://docs.micronaut.io/latest/guide/index.html#httpClient)

## Feature openapi documentation

- [Micronaut OpenAPI Support documentation](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/index.html)

- [https://www.openapis.org](https://www.openapis.org)

