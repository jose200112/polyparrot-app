# HTTPS con dominio propio: certificado ACM validado por DNS en Cloudflare.
#
# El token de Cloudflare NO va en el código: se lee de la variable de entorno
# CLOUDFLARE_API_TOKEN (ver README).

locals {
  fqdn = "${var.subdomain}.${var.domain_name}"
}

provider "cloudflare" {
  # api_token se toma de CLOUDFLARE_API_TOKEN
}

# 1) Certificado público de ACM (gratuito) para polyparrot.<dominio>
resource "aws_acm_certificate" "main" {
  domain_name       = local.fqdn
  validation_method = "DNS"

  lifecycle {
    create_before_destroy = true
  }

  tags = { Name = "${var.project_name}-cert" }
}

# 2) Registro CNAME que pide ACM para demostrar que el dominio es tuyo
resource "cloudflare_dns_record" "cert_validation" {
  for_each = {
    for dvo in aws_acm_certificate.main.domain_validation_options : dvo.domain_name => {
      name    = trimsuffix(dvo.resource_record_name, ".")
      content = trimsuffix(dvo.resource_record_value, ".")
      type    = dvo.resource_record_type
    }
  }

  zone_id = var.cloudflare_zone_id
  name    = each.value.name
  type    = each.value.type
  content = each.value.content
  ttl     = 60
  proxied = false
  comment = "Validacion ACM - gestionado por Terraform"
}

# 3) Espera a que ACM valide el certificado (puede tardar unos minutos)
resource "aws_acm_certificate_validation" "main" {
  certificate_arn = aws_acm_certificate.main.arn
  depends_on      = [cloudflare_dns_record.cert_validation]
}

# 4) polyparrot.<dominio> -> ALB. proxied=false: DNS directo, sin proxy de
#    Cloudflare, para que el TLS lo termine el ALB con el certificado de ACM.
resource "cloudflare_dns_record" "site" {
  zone_id = var.cloudflare_zone_id
  name    = local.fqdn
  type    = "CNAME"
  content = aws_lb.main.dns_name
  ttl     = 60
  proxied = false
  comment = "ALB de Polyparrot - gestionado por Terraform"
}

output "site_url" {
  description = "URL con HTTPS y dominio propio"
  value       = "https://${local.fqdn}"
}
