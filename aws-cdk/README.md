# Deploying Page-Builder with the AWS CDK in TypeScript

* `aws sso login`
* `aws sts get-caller-identity`

Configuring environments:
* `export CDK_DEFAULT_ACCOUNT=<aws-account-id>`
* `export CDK_DEFAULT_REGION=ap-southeast-2`
* `export VPC_NAME=<vpc-name>`
* `export DOMAIN_NAME=company.com`

AWS Secrets
* Google secret: dev/${stack.stackName}/google/credentials
** client_id
** client_secret
* ExchangeRatesApi secret: dev/${stack.stackName}/exchangeRates/credentials
** access_key

Deploy stack:
* `alias cdk="npx aws-cdk"`
* `cdk bootstrap`
* `cdk diff`
* `cdk deploy`

Destroy stack:
* `cdk destroy`
* `aws sso logout`

# References
* [Working with the AWS CDK in TypeScript](https://docs.aws.amazon.com/cdk/v2/guide/work-with-cdk-typescript.html)
* [Creating an AWS Fargate service using the AWS CDK](https://docs.aws.amazon.com/cdk/v2/guide/ecs_example.html)
* [Configuring environments](https://docs.aws.amazon.com/cdk/v2/guide/environments.html)
* [ASW CDK Examples](https://github.com/aws-samples/aws-cdk-examples/tree/main/typescript)
