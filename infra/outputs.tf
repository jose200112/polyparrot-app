output "instance_id" {
  value = aws_instance.app.id
}

output "ssm_command" {
  value = "aws ssm start-session --target ${aws_instance.app.id} --region ${var.aws_region}"
}
