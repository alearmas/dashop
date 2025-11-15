# Dashop Infrastructure

This repository contains the AWS SAM templates that provision the backend for Dashop, a serverless inventory management application. The templates define API Gateway endpoints, Lambda functions, and DynamoDB tables used by the service.

## License
This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

## Required tools
- [AWS SAM CLI](https://github.com/aws/aws-sam-cli)
- [AWS CLI](https://aws.amazon.com/cli/)
- An AWS account with permissions to deploy the stack

## Handler notation
Lambda functions are written in Java. When declaring a function in SAM, the `Handler` property should reference the fully qualified class name followed by `::handleRequest`:

```yaml
Handler: com.example.service.handler.MyHandler::handleRequest
```

All function templates use this pattern for consistency.

## Validating the Templates
You can use the AWS SAM CLI to validate the CloudFormation templates before deploying. Run the following command in the repository root:

```bash
sam validate --template main.yaml
```

Alternatively, you can use the provided `Makefile` to run the validation:

```bash
make validate
```

The `validate` target simply runs `sam validate` with the main template.

## Deployment
Run the following commands from the repository root:

```bash
sam build
sam deploy
```

`sam build` packages the Lambda functions and prepares the CloudFormation template. `sam deploy` deploys the generated template to your AWS account using the parameters in `samconfig.toml`.
