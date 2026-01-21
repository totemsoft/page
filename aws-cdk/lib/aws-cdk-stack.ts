import * as cdk from 'aws-cdk-lib/core';
import { Construct } from 'constructs';
import { AwsLogDriver, Cluster, ContainerDefinition, ContainerImage, FargateTaskDefinition } from 'aws-cdk-lib/aws-ecs';
import { ApplicationLoadBalancedFargateService } from 'aws-cdk-lib/aws-ecs-patterns';
import { ApplicationProtocol, Protocol } from 'aws-cdk-lib/aws-elasticloadbalancingv2';
import { HostedZone } from 'aws-cdk-lib/aws-route53';
import { EnvironmentUtils } from './include/environment-utils';
import { PolicyStatement } from 'aws-cdk-lib/aws-iam';
import * as ec2 from 'aws-cdk-lib/aws-ec2';
import { Certificate, CertificateValidation } from 'aws-cdk-lib/aws-certificatemanager';

export interface AwsCdkStackProps extends cdk.StackProps {

  /**
   * The name of the VPC
   * @type {string}
   * @memberof AwsCdkStackProps
   */
  readonly vpcName?: string;

  /**
   * The zone domain e.g. example.com
   * @type {string}
   * @memberof AwsCdkStackProps
   */
  readonly domainName: string;

}

export class AwsCdkStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props: AwsCdkStackProps) {
    super(scope, id, props);

    // Java
    //const containerImage= 'totemsoft/page-builder'; // :latest
    //const taskCpu = 1024;
    //const taskMemoryLimitMiB = 2048;
    // GraalVM
    const containerImage= 'totemsoft/page-builder-graalvm:latest'; // :latest
    const taskCpu = 256;
    const taskMemoryLimitMiB = 512;

    const domainName = props.domainName;

    const vpc = ec2.Vpc.fromLookup(this, id, {vpcName: props.vpcName});

    const vpcSubnets: ec2.SubnetSelection = {
      subnetType: ec2.SubnetType.PUBLIC
    };

    const cluster = new Cluster(this, `${id}Cluster`, {
      vpc
    });

    const taskDef = new FargateTaskDefinition(this, `${id}TaskDefinition1`, {
      cpu: taskCpu,
      memoryLimitMiB: taskMemoryLimitMiB
    });
    taskDef.addToTaskRolePolicy(new PolicyStatement( {
        actions: [
            //'cognito-idp:Admin*',
            //'ses:*',
            's3:*'
        ],
        resources: ['*']
    }));

    // create a task definition with CloudWatch Logs
    const logDriver = new AwsLogDriver({
      streamPrefix: `ecs-${id}`
    });
    const containerDef = new ContainerDefinition(this, `${id}ContainerDefinition`, {
      image: ContainerImage.fromRegistry(containerImage),
      taskDefinition: taskDef,
      environment: {
        STAGE: 'dev',
      },
      logging: logDriver,
      portMappings: [
        { containerPort: 8080, name: 'page-builder-http' }
      ]
    });

    EnvironmentUtils.addEnvironments(id, containerDef);

    const domainZone = HostedZone.fromLookup(this, 'Zone', {
      domainName
    });

    const certificate = new Certificate(this, `${id}Certificate`, {
      domainName: `${id}.${domainName}`,
      validation: CertificateValidation.fromDns(domainZone)
    });

    const sg = new ec2.SecurityGroup(this, `${id}System`, {
      vpc,
      allowAllOutbound: false,
      description: `${id} ALB`,
      securityGroupName: `${id}ALB`
    });
    sg.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(443), 'Inbound HTTPS');
    sg.addEgressRule(ec2.Peer.anyIpv4(), ec2.Port.allTcp(), 'Outbound');

    // Create a load-balanced Fargate service and make it public
    const albFargateService = new ApplicationLoadBalancedFargateService(this, `${id}FargateService`, {
      cluster,
      taskDefinition: taskDef,
      taskSubnets: vpcSubnets,
      desiredCount: 1,
      publicLoadBalancer: true,
      assignPublicIp: true,
      domainName: `${id}.${domainName}`,
      domainZone,
      certificate,
      protocol: ApplicationProtocol.HTTPS,
      targetProtocol: ApplicationProtocol.HTTP,
      securityGroups: [sg],
      minHealthyPercent: 50,
      idleTimeout: cdk.Duration.seconds(60),
      healthCheckGracePeriod: cdk.Duration.seconds(120)
    });

    const targetGroup = albFargateService.targetGroup;
    targetGroup.setAttribute('deregistration_delay.timeout_seconds', '30');
    targetGroup.configureHealthCheck({
      healthyHttpCodes: '200,302',
      path: '/',
      protocol: Protocol.HTTP,
      port: '8080',
      healthyThresholdCount: 3,
      interval: cdk.Duration.seconds(30)
    });
  }
}
