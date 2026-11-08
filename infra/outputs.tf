output "instance_id" {
  value = aws_instance.app.id
}

output "ssm_command" {
  value = "aws ssm start-session --target ${aws_instance.app.id} --region ${var.aws_region}"
}

output "app_url" {
  value = "http://${aws_lb.main.dns_name}"
}
