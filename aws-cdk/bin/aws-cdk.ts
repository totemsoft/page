#!/usr/bin/env node
import * as cdk from 'aws-cdk-lib/core';
import { AwsCdkStack } from '../lib/aws-cdk-stack';

const stackId = 'page-builder';
const terminationProtection = false;
const vpcName = process.env.VPC_NAME;
let domainName = process.env.DOMAIN_NAME;
if (domainName === undefined) {
  domainName = 'company.com';
}

const app = new cdk.App();

new AwsCdkStack(app, stackId, {
    // StackProps
    env: {
      account: process.env.CDK_DEFAULT_ACCOUNT,
      region: process.env.CDK_DEFAULT_REGION
    },
    stackName: stackId,
    description: `${stackId} Page Builder Stack`,
    tags: {'Name': `${stackId} Page Builder`},
    terminationProtection: terminationProtection,
    // AwsCdkStackProps
    vpcName: vpcName,
    domainName: domainName
  }
);
