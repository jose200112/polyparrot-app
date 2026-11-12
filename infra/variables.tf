variable "aws_region" {
  description = "Región de AWS donde se despliega"
  type        = string
  default     = "eu-north-1"
}

variable "project_name" {
  description = "Nombre del proyecto, se usa en nombres y etiquetas"
  type        = string
  default     = "polyparrot"
}

variable "instance_type" {
  description = "Tipo de instancia EC2 (x86; evita los t4g, que son ARM)"
  type        = string
  default     = "t3.large"
}

variable "repo_url" {
  description = "Repositorio público con el docker-compose de Polyparrot"
  type        = string
  default     = "https://github.com/jose200112/polyparrot-app.git"
}

variable "domain_name" {
  description = "Dominio registrado en Cloudflare (cambiarlo aquí si algún día cambia el dominio)"
  type        = string
  default     = "josemunozmunoz.dev"
}

variable "subdomain" {
  description = "Subdominio donde se publica la app (queda subdomain.domain_name)"
  type        = string
  default     = "polyparrot"
}

variable "cloudflare_zone_id" {
  description = "Zone ID del dominio en Cloudflare (no es secreto; el token va por variable de entorno)"
  type        = string
  default     = "8163ce5e19f2cee952184fa10af6e2ed"
}
